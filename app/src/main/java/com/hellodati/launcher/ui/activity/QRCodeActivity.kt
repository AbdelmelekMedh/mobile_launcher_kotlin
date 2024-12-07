package com.hellodati.launcher.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.hellodati.launcher.databinding.ActivityQrcodeBinding
import com.hellodati.launcher.R
import java.io.IOException
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.hellodati.launcher.ui.classes.QRCodeData
import java.io.File
import java.io.FileWriter

class QRCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQrcodeBinding
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""
    private lateinit var hotelLinksPreferences: SharedPreferences

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        hotelLinksPreferences = getSharedPreferences("hotel-links", MODE_PRIVATE)

        if (ContextCompat.checkSelfPermission(
                this@QRCodeActivity, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }

        val aniSlide: Animation =
            AnimationUtils.loadAnimation(this@QRCodeActivity, R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)
    }

    private fun writeJsonToFile(jsonData: String) {
        try {
            // Get the path to the external storage directory or internal storage directory
            val storageDir: File = Environment.getExternalStorageDirectory() // External storage
            // val storageDir: File = filesDir // Internal storage

            // Specify the file name
            val jsonFile = File(storageDir, "hotelLinks.json")

            // Create a FileWriter to write to the file
            val writer = FileWriter(jsonFile)

            // Use BufferedWriter for better performance
            // val writer = BufferedWriter(FileWriter(jsonFile))

            // Write JSON data to the file
            writer.write(jsonData)

            // Close the writer
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        binding.cameraSurfaceView.getHolder().addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue


                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    runOnUiThread {
                        cameraSource.stop()
                        val gson = Gson()

                        val result = gson.fromJson(scannedValue, QRCodeData::class.java)


                        val api_graphql_server = result.baseUrl + "/api/graphql"
                        val api_files_server = result.baseUrl + "/files"
                        val api_socket_server = "wss://" + result.baseUrl.replace("https://", "") + "/api/graphql"

                        hotelLinksPreferences.edit().putString("api_graphql_server", api_graphql_server).apply()
                        hotelLinksPreferences.edit().putString("api_files_server", api_files_server).apply()
                        hotelLinksPreferences.edit().putString("api_socket_server", api_socket_server).apply()

                        redirectToConnection();

                        finish()
                    }
                }else
                {
                    Toast.makeText(this@QRCodeActivity, "value- else", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@QRCodeActivity,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    private fun redirectToConnection() {
        val intent = Intent(applicationContext, ConnectionActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }

}