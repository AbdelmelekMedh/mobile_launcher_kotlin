package com.hellodati.launcher.ui.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer.OnCompletionListener
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import com.hellodati.launcher.R
import com.hellodati.launcher.api.GraphQLClient
import com.hellodati.launcher.api.PhoneNotifListener
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.api.RoomPhones
import com.hellodati.launcher.databinding.ActivityVideoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class
VideoActivity : AppCompatActivity(), SensorEventListener {
    lateinit var binding: ActivityVideoBinding
    private lateinit var sensorManager: SensorManager
    private var lastUpdate: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    private val shakeThreshold = 600
    private var shakeCount = 0
    private val requiredShakes = 10
    lateinit var myDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        myDialog = Dialog(this)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        Log.e("ddd", GraphQLClient(applicationContext).getGsfId().toString())
        val path = "android.resource://" + packageName + "/" + R.raw.video
        binding.videoView.setVideoURI(Uri.parse(path))
        binding.videoView.start()
        binding.videoView.setOnCompletionListener(
            OnCompletionListener {
                binding.videoView.seekTo(1)
                binding.videoView.start()
            })

        binding.wifiView.setOnClickListener {
            val wifiApp =
                binding.root.context.packageManager.getLaunchIntentForPackage("com.hellodati.wificonfig")
            if (wifiApp != null) {
                binding.root.context.startActivity(wifiApp)
            } else {
                Toast.makeText(
                    binding.root.context,
                    binding.root.context.getString(R.string.error_datiphone),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        GlobalScope.launch {
            val residenceClient = ResidenceClient(applicationContext)
            val residence = residenceClient.byGsfId()
            val savedResidenceId = residenceClient.getResidenceId()
            if(residence?.id == savedResidenceId) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(intent)
            }
        }
    }
    override fun onBackPressed() {}
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        val path = "android.resource://" + packageName + "/" + R.raw.video
        binding.videoView.setVideoURI(Uri.parse(path))
        binding.videoView.start()
        binding.videoView.setOnCompletionListener(
            OnCompletionListener {
                binding.videoView.seekTo(1)
                binding.videoView.start()
            })
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val curTime = System.currentTimeMillis()
        if (curTime - lastUpdate > 100) {
            val diffTime = curTime - lastUpdate
            lastUpdate = curTime

            val x = event!!.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

            if (speed > shakeThreshold) {
                shakeCount++
                if (shakeCount == requiredShakes) {
                    onShakeDetected()
                    shakeCount = 0
                }
            }

            lastX = x
            lastY = y
            lastZ = z
        }
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    private fun onShakeDetected() {
        showPopup()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun showPopup() {
        myDialog.setContentView(R.layout.dialog_device_shake)
        val graphQLClient = GraphQLClient(applicationContext)
        val btnOk: CardView = myDialog.findViewById<CardView>(R.id.btn_ok)
        val imei: TextView = myDialog.findViewById<TextView>(R.id.txt_gsf_device)
        val versionApp: TextView = myDialog.findViewById<TextView>(R.id.txt_version_app)
        val roomNumber: TextView = myDialog.findViewById<TextView>(R.id.txt_room_number)
        val btnClose: ImageView = myDialog.findViewById(R.id.btn_close)
        imei.text = graphQLClient.getGsfId()
        try {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        val roomPhones = RoomPhones(context = applicationContext)
                        Log.e("roomLog", graphQLClient.getGsfId().toString())
                        Log.e("roomLog", roomPhones.getRoomByGsfId(graphQLClient.getGsfId().toString()).toString())
                        roomNumber.text = roomPhones.getRoomByGsfId(graphQLClient.getGsfId().toString())!!.number.toInt().toString()
                    } catch (e: Exception) {
                        Log.e("roomLogExce", e.message.toString())
                    }

                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("roomLog", e.message.toString())
        }
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            versionApp.text = pInfo.versionName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        btnOk.setOnClickListener {
            myDialog.dismiss()
        }

        btnClose.setOnClickListener {
            myDialog.dismiss()
        }

        myDialog.show()
    }
}