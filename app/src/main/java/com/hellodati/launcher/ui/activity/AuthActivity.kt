package com.hellodati.launcher.ui.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.hellodati.launcher.R
import com.hellodati.launcher.Residence_byGsfIdQuery
import com.hellodati.launcher.api.GlobalSettingsClient
import com.hellodati.launcher.api.GraphQLClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.api.RoomPhones
import com.hellodati.launcher.database.AppDataBase
import com.hellodati.launcher.databinding.ActivityAuthBinding
import com.hellodati.launcher.type.InitializeGuestCodeInput
import com.hellodati.launcher.ui.helper.LocalHelper
import com.ncorti.slidetoact.SlideToActView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity(), SensorEventListener {
    lateinit var binding: ActivityAuthBinding
    private lateinit var sensorManager: SensorManager
    private var lastUpdate: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    private val shakeThreshold = 600
    private var shakeCount = 0
    private val requiredShakes = 10
    private val handler = Handler()
    lateinit var myDialog: Dialog
    private lateinit var hotelLinksPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hotelLinksPreferences = getSharedPreferences("hotel-links", MODE_PRIVATE)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myDialog = Dialog(this)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.btnSuivant.isLocked = true

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val globalSettingsClient = GlobalSettingsClient(applicationContext)

                    Glide.with(binding.root.context)
                        .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/globalSettings_pictures/${globalSettingsClient.getSettings()!!.id}_${globalSettingsClient.getSettings()!!.cover}?height=300")
                        .into(binding.hotelImage)

                    Glide.with(binding.root.context)
                        .load(hotelLinksPreferences.getString("api_files_server", null) + "/picture/globalSettings_pictures/${globalSettingsClient.getSettings()!!.id}_${globalSettingsClient.getSettings()!!.defaultLogo}?height=300")
                        .into(binding.hotelLogo)

                    binding.hotelStars.rating =
                        globalSettingsClient.getSettings()!!.rating.toFloat()

                    when (LocalHelper.getLanguage(binding.root.context).toString()) {
                        "en" -> {
                            if (globalSettingsClient.getSettings()!!.name.en.toString() != "null") {
                                binding.hotelTitle.text =
                                    globalSettingsClient.getSettings()!!.name.en.toString()
                            } else {
                                binding.hotelTitle.text =
                                    globalSettingsClient.getSettings()!!.name.default.toString()
                            }

                        }

                        "ar" -> {
                            if (globalSettingsClient.getSettings()!!.name.ar.toString() != "null") {
                                binding.hotelTitle.text =
                                    globalSettingsClient.getSettings()!!.name.ar.toString()
                            } else {
                                binding.hotelTitle.text =
                                    globalSettingsClient.getSettings()!!.name.default.toString()
                            }

                        }

                        "fr" -> {
                            if (globalSettingsClient.getSettings()!!.name.fr.toString() != "null") {
                                binding.hotelTitle.text =
                                    globalSettingsClient.getSettings()!!.name.fr.toString()
                            } else {
                                binding.hotelTitle.text =
                                    globalSettingsClient.getSettings()!!.name.default.toString()
                            }

                        }

                        else -> {
                            binding.hotelTitle.text =
                                globalSettingsClient.getSettings()!!.name.default.toString()
                        }
                    }
                } catch (e: Exception) {

                }

            }
        }

        binding.btnSuivant.setOnClickListener {
            if (binding.guestCode.text.toString().length == 4) {
                if (binding.guestCodeConfirmation.text.toString() == binding.guestCode.text.toString()) {
                    binding.btnSuivant.isLocked = false
                } else {
                    binding.guestCodeConfirmation.requestFocus()
                    Toast.makeText(
                        binding.root.context,
                        resources.getString(R.string.user_id_cnfr_required),
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnSuivant.isLocked = true
                }
            } else {
                binding.guestCode.requestFocus()
                Toast.makeText(
                    binding.root.context,
                    resources
                        .getString(R.string.user_id_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        GlobalScope.launch {
            try {
                val residenceClient = ResidenceClient(applicationContext)
                val residence = residenceClient.byGsfId()
                initializeNextButton(residence, binding)
            }catch (e: Exception) {
                Log.e("exception ", e.message.toString())
            }
        }
    }

    private fun initializeNextButton(
        residence: Residence_byGsfIdQuery.Residence_byGsfId?,
        binding: ActivityAuthBinding
    ) {
        val slideToActCompleteListener = SlideToActCompleteListener(residence, binding)

        binding.btnSuivant.onSlideCompleteListener = slideToActCompleteListener
    }

    class SlideToActCompleteListener(
        residence: Residence_byGsfIdQuery.Residence_byGsfId?,
        binding: ActivityAuthBinding
    ) : SlideToActView.OnSlideCompleteListener {
        private val guestId: String? = residence?.guestId
        private val residenceId: String? = residence?.id
        private val authBinding = binding

        override fun onSlideComplete(view: SlideToActView) {
            hideKeyboard(view.context, view)
            try {
                GlobalScope.launch {
                    val guestClient = GuestClient(view.context)
                    val residenceClient = ResidenceClient(view.context)
                    val codeCreated = guestClient.initializeCode(
                        InitializeGuestCodeInput(
                            authBinding.guestCode.text.toString(),
                            guestId.toString(),
                            authBinding.guestCodeConfirmation.text.toString()
                        )
                    )

                    if (codeCreated as Boolean) {
                        residenceClient.saveResidenceId(residenceId)
                        residenceClient.saveGuestId(guestId)
                        val db = AppDataBase.getInstance(authBinding.root.context)
                        db!!.basketDao().deleteTable()
                        val intent = Intent(view.context, MainActivity::class.java)
                        view.context.startActivity(intent)
                    }
                }
            } catch (e: Exception) {
                Log.e("exception ", e.message.toString())
            }

        }

        fun hideKeyboard(context: Context, view: View) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
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
                    showPopup()
                    shakeCount = 0
                }
            }

            lastX = x
            lastY = y
            lastZ = z
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
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
                    val roomPhones = RoomPhones(context = applicationContext)
                    Log.e("roomLog", graphQLClient.getGsfId().toString())
                    Log.e("roomLog", roomPhones.getRoomByGsfId(graphQLClient.getGsfId().toString()).toString())
                    roomNumber.text = roomPhones.getRoomByGsfId(graphQLClient.getGsfId().toString())!!.number.toInt().toString()

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