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
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.hellodati.launcher.ui.helper.LocalHelper
import com.hellodati.launcher.R
import com.hellodati.launcher.api.GlobalSettingsClient
import com.hellodati.launcher.api.GraphQLClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.api.RoomPhones
import com.hellodati.launcher.ui.adapters.LanguageAdapter
import com.hellodati.launcher.databinding.ActivityWelcomeBinding
import com.hellodati.launcher.model.Language
import com.ncorti.slidetoact.SlideToActView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class WelcomeActivity : AppCompatActivity(), SensorEventListener {

    lateinit var dialog: Dialog

    private lateinit var binding: ActivityWelcomeBinding

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

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        myDialog = Dialog(this)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val residenceClient = ResidenceClient(applicationContext)
                    val residence = residenceClient.byGsfId()
                    val guestClient = GuestClient(applicationContext)
                    val guest = guestClient.getGuestById(residence!!.guestId.toString())
                    val globalSettingsClient = GlobalSettingsClient(applicationContext)

                    binding.guestName.text = "${guest!!.lastName} ${guest!!.firstName}"

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


        val defaultLocale = Locale.getDefault()

        binding.spinner.text = defaultLocale.language.uppercase()

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


        /*      binding.btnSuivant.setOnClickListener{
                  val intent = Intent(applicationContext, MainActivity::class.java)
                  startActivity(intent)
              }*/

        val slideToActCompleteListener = MySlideToActCompleteListener()

        binding.btnSuivant.onSlideCompleteListener = slideToActCompleteListener
        binding.spinner.setOnClickListener {
            showPopupLangsSelect()
        }
    }


    /* fun showPopupLangsSelect(context: Context) {
         val langs = listOf(
             Language("English", "en"),
             Language("Français", "fr"),
             Language("العربية", "ar")
         )

         val adapter = LanguageAdapter(context, langs)
         val defaultLocale = Locale.getDefault()
         adapter.setSelected(defaultLocale.language)
         val listItem: View =
             LayoutInflater.from(applicationContext).inflate(R.layout.popup_change_lang, null, false)
         dialog.setContentView(listItem)
         listItem.findViewById<View>(R.id.close).setOnClickListener { dialog.dismiss() }
         val langsListView = listItem.findViewById<ListView>(R.id.langs_list)
         langsListView.adapter = adapter
         langsListView.onItemClickListener =
             AdapterView.OnItemClickListener { adapterView, view, position, l ->
                 try {
                     val langSelected: Language = adapter.getItem(position)!!

                     if (langSelected.iso != LocalHelper.getLanguage(view.context)) {

                         val selectedLang: String = adapter.getItem(position)!!.iso
                         LocalHelper.setLocale(
                             applicationContext,
                             selectedLang
                         )


                         val locale = Locale(selectedLang)
                         Locale.setDefault(locale)
                         val config = resources.configuration
                         config.setLocale(locale)
                         baseContext.resources.updateConfiguration(config, resources.displayMetrics)

                         val intent = Intent(this, WelcomeActivity::class.java)
                         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                         startActivity(intent)

                         dialog.dismiss()
                     }

                 } catch (e: Exception) {

                 }
             }
         dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
         dialog.show()
     }*/

    fun showPopupLangsSelect() {

        val langs = listOf(
            Language("English", "en"),
            Language("Français", "fr"),
            Language("العربية", "ar")
        )

        val sharedPref = applicationContext.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        val adapter = LanguageAdapter(applicationContext, langs)
        val defaultLocale = Locale.getDefault()
        val langSaved = sharedPref.getString("language", defaultLocale.toString())
        // adapter.setSelected(defaultLocale.toString())
        Log.e("language", langSaved.toString())
        adapter.setSelected(langSaved)
        val listItem: View =
            LayoutInflater.from(applicationContext).inflate(R.layout.popup_change_lang, null, false)
        dialog.setContentView(listItem)
        listItem.findViewById<View>(R.id.close).setOnClickListener { dialog.dismiss() }
        val langsListView = listItem.findViewById<ListView>(R.id.langs_list)
        langsListView.adapter = adapter
        langsListView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, position, l ->
                try {
                    val langSelected: Language = adapter.getItem(position)!!

                    if (langSelected != null && langSelected.iso != LocalHelper.getLanguage(view.context)) {

                        val selectedLang: String = adapter.getItem(position)!!.iso

                        LocalHelper.setLocale(
                            applicationContext,
                            selectedLang
                        )

                        val locale = Locale(selectedLang)
                        Locale.setDefault(locale)
                        val config = resources.configuration
                        config.setLocale(locale)
                        resources.updateConfiguration(config, resources.displayMetrics)


                        val editor = sharedPref.edit()
                        editor.putString("language", selectedLang)
                        editor.apply()


                        //activity.recreate();
                        val intent = Intent(this, WelcomeActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        dialog.dismiss()
                    }

                } catch (e: Exception) {

                }
            }
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    override fun onBackPressed() {}


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
                    Log.e(
                        "roomLog",
                        roomPhones.getRoomByGsfId(graphQLClient.getGsfId().toString()).toString()
                    )
                    roomNumber.text = roomPhones.getRoomByGsfId(
                        graphQLClient.getGsfId().toString()
                    )!!.number.toInt().toString()

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


    class MySlideToActCompleteListener : SlideToActView.OnSlideCompleteListener {
        override fun onSlideComplete(view: SlideToActView) {
            val intent = Intent(view.context, AuthActivity::class.java)
            view.context.startActivity(intent)
        }
    }
}