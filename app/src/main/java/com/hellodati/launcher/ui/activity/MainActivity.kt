package com.hellodati.launcher.ui.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.AdapterView.OnItemClickListener
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView.OnGroupClickListener
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.material.navigation.NavigationView
import com.hellodati.launcher.Message_listenerSubscription
import com.hellodati.launcher.PhoneNotifSubscription
import com.hellodati.launcher.R
import com.hellodati.launcher.api.GraphQLClient
import com.hellodati.launcher.api.GuestClient
import com.hellodati.launcher.api.MarkAllAsReadClient
import com.hellodati.launcher.api.MobileConversationClient
import com.hellodati.launcher.api.PhoneNotifListener
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.api.RoomPhones
import com.hellodati.launcher.database.AppDataBase
import com.hellodati.launcher.databinding.ActivityMainBinding
import com.hellodati.launcher.databinding.NavHeaderMainBinding
import com.hellodati.launcher.databinding.PopupNotifAcceptedBinding
import com.hellodati.launcher.databinding.PopupNotifInfoBinding
import com.hellodati.launcher.databinding.PopupNotifRejectedBinding
import com.hellodati.launcher.model.Language
import com.hellodati.launcher.type.PhoneNotifTypeEnum
import com.hellodati.launcher.type.ReservationTypeEnum
import com.hellodati.launcher.ui.adapters.DrawerListAdapter
import com.hellodati.launcher.ui.adapters.LanguageAdapter
import com.hellodati.launcher.ui.helper.LocalHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.pow


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    private lateinit var bindingNavHeader: NavHeaderMainBinding
    private lateinit var badgeNavNotif: TextView

    private lateinit var sensorManager: SensorManager
    private var lastUpdate: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    private val shakeThreshold = 600
    private var shakeCount = 0
    private val requiredShakes = 10


    lateinit var dialog: Dialog
    lateinit var myDialog: Dialog

    private val handler = Handler()
    val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
    //private val networkCallback = MyNetworkCallback(this)

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val defaultLanguage = sharedPref.getString("language", Locale.getDefault().toString())
        // for get app language -> val language = Resources.getSystem().configuration.locale.language
        val locale = Locale(defaultLanguage)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingNavHeader = NavHeaderMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        myDialog = Dialog(this)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))





        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val guestClient = GuestClient(binding.root.context)
                    val result = guestClient.getGuestById(
                        ResidenceClient(binding.root.context).getGuestId().toString()
                    )
                    when (result) {
                        null -> {
                        }

                        else -> {
                            val nameProfile: TextView =
                                binding.navView.getHeaderView(0).findViewById(R.id.profile_name)
                            nameProfile.text = "${result!!.lastName} ${result!!.firstName}"

                            val emailProfile: TextView =
                                binding.navView.getHeaderView(0).findViewById(R.id.profile_mail)
                            emailProfile.text = "${result!!.email} "
                        }
                    }
                    val unreadNumber = MobileConversationClient(binding.root.context)
                        .getConversation(
                            ResidenceClient(binding.root.context).getGuestId().toString()
                        )!!
                        .unread_number_for_guest!!.toInt()
                    if (unreadNumber == 0) {
                        binding.chatBadgeNotification.visibility = View.GONE
                    } else {
                        binding.chatBadgeNotification.visibility = View.VISIBLE
                        binding.chatBadgeNotification.text = unreadNumber.toString()
                        binding.chatBadgeNotification.startAnimation(shakeBadge())
                    }
                } catch (e: Exception) {
                    Log.e("unreadNumber", e.message.toString())
                }
            }
        }


        /* binding.appBarMain.fab.setOnClickListener {
             findNavController(R.id.nav_host_fragment_content_main)
                 .navigate(R.id.homeFragment)
         } */

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_hotspot_switch -> {
                    val hotspotPackage =
                        applicationContext.packageManager.getLaunchIntentForPackage("com.hellodati.app.mobilehotspot")
                    if (hotspotPackage != null) {
                        GlobalScope.launch {
                            ResidenceClient(applicationContext).byGsfId()
                            applicationContext.startActivity(hotspotPackage)
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            applicationContext.getString(R.string.error_datiphone),
                            Toast.LENGTH_LONG
                        ).show()
                        false
                    }
                    /*   drawerLayout.openDrawer(GravityCompat.END);*/
                    false

                }

                R.id.nav_home -> {
                    findNavController(R.id.nav_host_fragment_content_main)
                        .navigate(R.id.notificationFragment)
                    drawerLayout.closeDrawer(GravityCompat.START);
                    false
                }

                else -> false
            }
        }

        binding.imgProfile.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.imgChat.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.chatFragment)
            drawerLayout.closeDrawer(GravityCompat.START)
            binding.chatBadgeNotification.visibility = View.GONE
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val guestId = ResidenceClient(binding.root.context).getGuestId().toString()
                    val guest = GuestClient(binding.root.context).getGuestById(guestId)
                    if (guest?.hasUnresolvedConversation == true) {
                        val mobileConvId =
                            MobileConversationClient(binding.root.context).getConversation(guestId)!!.id
                        if (mobileConvId != null) {
                            MarkAllAsReadClient(binding.root.context).MarkAllAsRead(
                                mobileConvId,
                                true
                            )
                        }
                    }
                } catch (e: ApolloException) {
                    Log.e("chatListener", e.toString())
                }
            }
        }

        val listDataHeader = listOf(
            resources.getString(R.string.drawer_item_commands),
            resources.getString(R.string.drawer_item_reservations),
            resources.getString(R.string.drawer_item_dashboard),
            resources.getString(R.string.drawer_item_about)
        )
        val listDataChild = mapOf(
            resources.getString(R.string.drawer_item_dashboard) to listOf(
                resources.getString(R.string.drawer_item_wifi),
                resources.getString(R.string.drawer_item_language)
            )
        )

        val listAdapter: ExpandableListAdapter =
            DrawerListAdapter(applicationContext, listDataHeader, listDataChild)
        binding.expListDrawer.setAdapter(listAdapter)

        binding.expListDrawer.setOnGroupClickListener(OnGroupClickListener { parent, v, groupPosition, id ->
            when (groupPosition) {
                0 -> {
                    findNavController(R.id.nav_host_fragment_content_main)
                        .navigate(R.id.orderHistoryFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                1 -> {
                    findNavController(R.id.nav_host_fragment_content_main)
                        .navigate(R.id.historyBookingFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }

                3 -> {
                    findNavController(R.id.nav_host_fragment_content_main)
                        .navigate(R.id.aboutItemNavFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            false
        })

        binding.expListDrawer.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            when (groupPosition) {
                2 -> {
                    if (childPosition == 0) {

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

                    if (childPosition == 1) {
                        showPopupLangsSelect()

                    }

                    if (childPosition == 2) {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                        startActivity(intent)

                    }
                }
            }
            return@setOnChildClickListener true
        }


        binding.appBarMain.navHomeBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.homeFragment)
        }

        binding.appBarMain.navGuideBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.guideFragment)
        }

        binding.appBarMain.navAppsBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.appsFragment)
        }

        binding.appBarMain.navCallsBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:")
            startActivity(intent)
            /*   findNavController(R.id.nav_host_fragment_content_main)
                   .navigate(R.id.callsFragment)*/
        }

        binding.appBarMain.navBasketBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment_content_main)
                .navigate(R.id.basketFragment)
        }

        setTextView(binding.appBarMain.txtBadgeNotification)
        updateTextView(
            AppDataBase.getInstance(binding.root.context)!!.basketDao().getAll().size.toString()
        )

        binding.appBarMain.txtBadgeNotification.startAnimation(shakeBadge())

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val graphQLClient = GraphQLClient(binding.root.context)
                val gsfId = graphQLClient.getGsfId()

                val phoneNotificationFlow = graphQLClient.client
                    .subscription(PhoneNotifSubscription(gsfId = gsfId.toString()))
                    .toFlow()
                    .retryWhen { e, attempt ->
                        delay(2.0.pow(attempt.toDouble()).toLong())
                        true
                    }
                    .map { it.data?.phoneNotif?.type }

                val messageNotificationFlow = graphQLClient.client
                    .subscription(Message_listenerSubscription())
                    .toFlow()
                    .retryWhen { e, attempt ->
                        delay(2.0.pow(attempt.toDouble()).toLong())
                        true
                    }
                    .map {
                        MobileConversationClient(binding.root.context)
                            .getConversation(
                                ResidenceClient(binding.root.context).getGuestId().toString()
                            )?.unread_number_for_guest?.toInt() ?: 0
                    }

                merge(phoneNotificationFlow, messageNotificationFlow)
                    .collect { notification ->
                        when (notification) {
                            is PhoneNotifTypeEnum -> handlePhoneNotification(notification)
                            is Int -> handleMessageNotification(notification)
                            else -> {} // Handle other cases if needed
                        }
                    }
            } catch (e: ApolloException) {
                Log.e("notification", e.toString())
            }
        }

        binding.btnTripadvisor.setOnClickListener {
            val intent = Intent(applicationContext, TripAdvisorActivity::class.java)
            startActivity(intent)
        }

        binding.clearData.setOnClickListener {
            myDialog.setContentView(R.layout.dialog_clear_data)
            val btnOk: CardView = myDialog.findViewById<CardView>(R.id.btn_ok)
            val btnClose: ImageView = myDialog.findViewById(R.id.btn_close)


            btnOk.setOnClickListener {
                Toast.makeText(
                    binding.root.context,
                    getString(R.string.msg_data_deleted),
                    Toast.LENGTH_LONG
                ).show()
                myDialog.dismiss()
            }

            btnClose.setOnClickListener {
                myDialog.dismiss()
            }

            myDialog.show()
        }
    }

    override fun onBackPressed() {
        supportFragmentManager.popBackStack()
        binding.imgProfile.visibility = View.VISIBLE
        binding.imgChat.visibility = View.VISIBLE
        binding.appBarMain.linearLayout6.visibility = View.VISIBLE
        binding.drawerLayout.closeDrawer(GravityCompat.START,true)
    }

    private fun handleMessageNotification(notification: Int) {
        try {
            if (notification == 0) {
                binding.chatBadgeNotification.visibility = View.GONE
            } else {
                mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.facebook_messenger)
                mediaPlayer.start()
                binding.chatBadgeNotification.visibility = View.VISIBLE
                binding.chatBadgeNotification.text = notification.toString()
                binding.chatBadgeNotification.startAnimation(shakeBadge())
            }
        } catch (e: ApolloException) {
            Log.e("chatListener", e.toString())
        }
    }

    private fun handlePhoneNotification(notification: PhoneNotifTypeEnum) {
        when (notification) {
            PhoneNotifTypeEnum.order_rejected -> orderRejected()
            PhoneNotifTypeEnum.order_in_progress -> orderProcessed()
            PhoneNotifTypeEnum.order_ready -> orderReadyForDeliver()
            PhoneNotifTypeEnum.order_in_delivery-> orderInDeliver()
            PhoneNotifTypeEnum.order_delivered -> orderDelivered()
            PhoneNotifTypeEnum.concierge_request_reject -> conciergeReject()
                PhoneNotifTypeEnum.concierge_request_accept ->conciergeAccept()
            PhoneNotifTypeEnum.resevation_reject-> reservationRejected()
            PhoneNotifTypeEnum.resevation_accept -> reservationAccepted()
            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDialog.dismiss()
    }

    private fun initItemsOfNavigationDrawer() {
        val listDataHeader: MutableList<String> = ArrayList()
        val listHash = HashMap<String, List<String>>()
        listDataHeader.add(resources.getString(R.string.drawer_item_commands))
        listDataHeader.add(resources.getString(R.string.drawer_item_reservations))
        listDataHeader.add(resources.getString(R.string.drawer_item_dashboard))
        listDataHeader.add(resources.getString(R.string.drawer_item_about))

        val commands: List<String> = ArrayList()
        val reservations: List<String> = ArrayList()

        val tab_bord: MutableList<String> = ArrayList()
        tab_bord.add(resources.getString(R.string.drawer_item_wifi))

        tab_bord.add(resources.getString(R.string.drawer_item_language))


        val about: List<String> = ArrayList()

        listHash[listDataHeader[0]] = commands
        listHash[listDataHeader[1]] = reservations
        listHash[listDataHeader[2]] = tab_bord
        listHash[listDataHeader[3]] = about

        /*        val listAdapter: ExpandableListAdapter = DrawerListAdapter(listHash,listDataHeader,applicationContext)
                binding.expListDrawer.setAdapter(listAdapter)*/
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


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
        adapter.setSelected(langSaved)
        val listItem: View =
            LayoutInflater.from(applicationContext).inflate(R.layout.popup_change_lang, null, false)
        dialog.setContentView(listItem)
        listItem.findViewById<View>(R.id.close).setOnClickListener { dialog.dismiss() }
        val langsListView = listItem.findViewById<ListView>(R.id.langs_list)
        langsListView.adapter = adapter
        langsListView.onItemClickListener =
            OnItemClickListener { adapterView, view, position, l ->
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
                        val intent = Intent(this, MainActivity::class.java)
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun onShakeDetected() {
        showPopup()
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

    private fun orderRejected() {
        val bindingDialog =
            PopupNotifRejectedBinding.inflate(LayoutInflater.from(binding.root.context))
        val contextRef = WeakReference(binding.root.context)
        val myDialog = contextRef.get()?.let { Dialog(it) }
        myDialog?.setContentView(bindingDialog.root)
        myDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.window!!.attributes.gravity = Gravity.TOP

        lifecycleScope.launch(Dispatchers.Main) {
            val dayNameMap = mapOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thrs",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            val phoneNotifListener = PhoneNotifListener(binding.root.context)
            val listNotif = phoneNotifListener.getGuestNotifications(
                ResidenceClient(binding.root.context).getResidenceId().toString()
            ).groupBy { notification ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(notification.createdAt.toString())
                calendar.time = date
                calendar.get(Calendar.DAY_OF_WEEK)

            }.mapKeys { dayNameMap[it.key] }.toList()
            if (listNotif.isNotEmpty()){
                val phoneNotifList = listNotif[listNotif.lastIndex]!!.second[0]
                bindingDialog.notificationContent.text =
                    bindingDialog.root.context.resources.getString(R.string.your_order) + " " + phoneNotifList.mobileOrder!!.orderNumber + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_rejected
                    )
            }else{
                bindingDialog.notificationContent.text = bindingDialog.root.context.resources.getString(R.string.your_order) + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_rejected
                    )
            }
        }
        if (!myDialog.isShowing && !this.isFinishing) {
            myDialog.show()
        }
    }

    private fun orderProcessed() {
        val bindingDialog =
            PopupNotifAcceptedBinding.inflate(LayoutInflater.from(binding.root.context))
        val contextRef = WeakReference(binding.root.context)
        val myDialog = contextRef.get()?.let { Dialog(it) }
        myDialog?.setContentView(bindingDialog.root)
        myDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.window!!.attributes.gravity = Gravity.TOP

        lifecycleScope.launch(Dispatchers.Main) {
            val dayNameMap = mapOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thrs",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            val phoneNotifListener = PhoneNotifListener(binding.root.context)
            val listNotif = phoneNotifListener.getGuestNotifications(
                ResidenceClient(binding.root.context).getResidenceId().toString()
            ).groupBy { notification ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(notification.createdAt.toString())
                calendar.time = date
                calendar.get(Calendar.DAY_OF_WEEK)

            }.mapKeys { dayNameMap[it.key] }.toList()
            if (listNotif.isNotEmpty()){
                val phoneNotifList = listNotif[listNotif.lastIndex]!!.second[0]
                bindingDialog.notificationContent.text =
                    bindingDialog.root.context.resources.getString(R.string.your_order) + " " + phoneNotifList.mobileOrder!!.orderNumber + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_in_progress
                    )
            }else{
                bindingDialog.notificationContent.text = bindingDialog.root.context.resources.getString(R.string.your_order) + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_in_progress
                    )
            }
        }
        if (!myDialog.isShowing && !this.isFinishing) {
            myDialog.show()
        }
    }

    private fun orderReadyForDeliver() {
        val bindingDialog = PopupNotifInfoBinding.inflate(LayoutInflater.from(binding.root.context))
        val contextRef = WeakReference(binding.root.context)
        val myDialog = contextRef.get()?.let { Dialog(it) }
        myDialog?.setContentView(bindingDialog.root)
        myDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.window!!.attributes.gravity = Gravity.TOP

        lifecycleScope.launch(Dispatchers.Main) {
            val dayNameMap = mapOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thrs",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            val phoneNotifListener = PhoneNotifListener(binding.root.context)
            val listNotif = phoneNotifListener.getGuestNotifications(
                ResidenceClient(binding.root.context).getResidenceId().toString()
            ).groupBy { notification ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(notification.createdAt.toString())
                calendar.time = date
                calendar.get(Calendar.DAY_OF_WEEK)

            }.mapKeys { dayNameMap[it.key] }.toList()
            if (listNotif.isNotEmpty()){
                val phoneNotifList = listNotif[listNotif.lastIndex]!!.second[0]
                bindingDialog.notificationContent.text =
                    bindingDialog.root.context.resources.getString(R.string.your_order) + " " + phoneNotifList.mobileOrder!!.orderNumber + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_item_ready
                    )
            }else{
                bindingDialog.notificationContent.text = bindingDialog.root.context.resources.getString(R.string.your_order) + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_item_ready
                    )
            }
        }
        if (!myDialog.isShowing && !this.isFinishing) {
            myDialog.show()
        }
    }

    private fun orderInDeliver() {
        val bindingDialog = PopupNotifInfoBinding.inflate(LayoutInflater.from(binding.root.context))
        val contextRef = WeakReference(binding.root.context)
        val myDialog = contextRef.get()?.let { Dialog(it) }
        myDialog?.setContentView(bindingDialog.root)
        myDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.window!!.attributes.gravity = Gravity.TOP

        lifecycleScope.launch(Dispatchers.Main) {
            val dayNameMap = mapOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thrs",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            val phoneNotifListener = PhoneNotifListener(binding.root.context)
            val listNotif = phoneNotifListener.getGuestNotifications(
                ResidenceClient(binding.root.context).getResidenceId().toString()
            ).groupBy { notification ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(notification.createdAt.toString())
                calendar.time = date
                calendar.get(Calendar.DAY_OF_WEEK)

            }.mapKeys { dayNameMap[it.key] }.toList()
            if (listNotif.isNotEmpty()){
                val phoneNotifList = listNotif[listNotif.lastIndex]!!.second[0]
                bindingDialog.notificationContent.text = bindingDialog.root.context.resources.getString(R.string.your_order) + " " + phoneNotifList.mobileOrder!!.orderNumber + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_in_delivery
                    )
            }else{
                bindingDialog.notificationContent.text = bindingDialog.root.context.resources.getString(R.string.your_order) + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_in_delivery
                    )
            }

        }
        if (!myDialog.isShowing && !this.isFinishing) {
            myDialog.show()
        }
    }

    private fun orderDelivered() {
        val bindingDialog =
            PopupNotifAcceptedBinding.inflate(LayoutInflater.from(binding.root.context))
        val contextRef = WeakReference(binding.root.context)
        val myDialog = contextRef.get()?.let { Dialog(it) }
        myDialog?.setContentView(bindingDialog.root)
        myDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.window!!.attributes.gravity = Gravity.TOP

        lifecycleScope.launch(Dispatchers.Main) {
            val dayNameMap = mapOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thrs",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            val phoneNotifListener = PhoneNotifListener(binding.root.context)
            val listNotif = phoneNotifListener.getGuestNotifications(
                ResidenceClient(binding.root.context).getResidenceId().toString()
            ).groupBy { notification ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(notification.createdAt.toString())
                calendar.time = date
                calendar.get(Calendar.DAY_OF_WEEK)

            }.mapKeys { dayNameMap[it.key] }.toList()
            if (listNotif.isNotEmpty()){
                val phoneNotifList = listNotif[listNotif.lastIndex]!!.second[0]
                bindingDialog.notificationContent.text =
                    bindingDialog.root.context.resources.getString(R.string.your_order) + " " + phoneNotifList.mobileOrder!!.orderNumber + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_delivered
                    )
            }else{
                bindingDialog.notificationContent.text =
                    bindingDialog.root.context.resources.getString(R.string.your_order) + " " + bindingDialog.root.context.resources.getString(
                        R.string.notif_order_delivered
                    )
            }
        }
        if (!myDialog.isShowing && !this.isFinishing) {
            myDialog.show()
        }
    }

    private fun reservationRejected() {
        val bindingDialog =
            PopupNotifRejectedBinding.inflate(LayoutInflater.from(binding.root.context))
        val contextRef = WeakReference(binding.root.context)
        val myDialog = contextRef.get()?.let { Dialog(it) }
        myDialog?.setContentView(bindingDialog.root)
        myDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.window!!.attributes.gravity = Gravity.TOP

        lifecycleScope.launch(Dispatchers.Main) {
            val dayNameMap = mapOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thrs",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            val phoneNotifListener = PhoneNotifListener(binding.root.context)
            val listNotif = phoneNotifListener.getGuestNotifications(
                ResidenceClient(binding.root.context).getResidenceId().toString()
            ).groupBy { notification ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(notification.createdAt.toString())
                calendar.time = date
                calendar.get(Calendar.DAY_OF_WEEK)

            }.mapKeys { dayNameMap[it.key] }.toList()
            if (listNotif.isNotEmpty()) {
                val phoneNotifList = listNotif[listNotif.lastIndex]!!.second[0]
                when (phoneNotifList.mobileReservation!!.reservationType) {
                    ReservationTypeEnum.concierge -> {
                        bindingDialog.notificationContent.text =
                            phoneNotifList.mobileConciergeRequest!!.concierge!!.title.default + " " + bindingDialog.root.context.getString(
                                R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                            )
                    }

                    ReservationTypeEnum.eateries -> {
                        bindingDialog.notificationContent.text =
                            phoneNotifList.mobileReservation!!.eateryservice!!.title.default + " " + bindingDialog.root.context.getString(
                                R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                            )
                    }

                    ReservationTypeEnum.events -> {
                        bindingDialog.notificationContent.text =
                            phoneNotifList.mobileReservation!!.eventService!!.title.default + " " + bindingDialog.root.context.getString(
                                R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                            )
                    }

                    ReservationTypeEnum.leisure -> {
                        bindingDialog.notificationContent.text =
                            phoneNotifList.mobileReservation!!.leisureService!!.title.default + " " + bindingDialog.root.context.getString(
                                R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                            )
                    }

                    ReservationTypeEnum.meeting -> {
                        bindingDialog.notificationContent.text =
                            phoneNotifList.mobileReservation!!.meetingService!!.title.default + " " + bindingDialog.root.context.getString(
                                R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                            )
                    }

                    ReservationTypeEnum.wellbeingitems -> {
                        bindingDialog.notificationContent.text =
                            bindingDialog.root.context.resources.getString(R.string.well_being) + " " + bindingDialog.root.context.getString(
                                R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                            )
                    }

                    else -> {
                        bindingDialog.notificationContent.text =
                            bindingDialog.root.context.resources.getString(R.string.your) + " " + bindingDialog.root.context.getString(
                                R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                            )
                    }
                }
            } else {
                bindingDialog.notificationContent.text =
                    bindingDialog.root.context.resources.getString(R.string.your) + " " + bindingDialog.root.context.getString(
                        R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                    )
            }
        }
        if (!myDialog.isShowing && !this.isFinishing) {
            myDialog.show()
        }
    }

    private fun reservationAccepted() {
        val bindingDialog =
            PopupNotifAcceptedBinding.inflate(LayoutInflater.from(binding.root.context))
        val contextRef = WeakReference(binding.root.context)
        val myDialog = contextRef.get()?.let { Dialog(it) }
        myDialog?.setContentView(bindingDialog.root)
        myDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.window!!.attributes.gravity = Gravity.TOP

        lifecycleScope.launch(Dispatchers.Main) {
            val dayNameMap = mapOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thrs",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            val phoneNotifListener = PhoneNotifListener(binding.root.context)
            val listNotif = phoneNotifListener.getGuestNotifications(
                ResidenceClient(binding.root.context).getResidenceId().toString()
            ).groupBy { notification ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(notification.createdAt.toString())
                calendar.time = date
                calendar.get(Calendar.DAY_OF_WEEK)

            }.mapKeys { dayNameMap[it.key] }.toList()
            if (listNotif.isNotEmpty()){
                val phoneNotifList = listNotif[listNotif.lastIndex]!!.second[0]
                when (phoneNotifList.mobileReservation!!.reservationType) {
                    ReservationTypeEnum.concierge -> {
                        bindingDialog.notificationContent.text =
                            bindingDialog.root.context.resources.getString(R.string.your) + " " + phoneNotifList.mobileConciergeRequest!!.concierge!!.title.default + " " + bindingDialog.root.context.resources.getString(
                                R.string.notif_booking_accepted_p2
                            )
                    }

                    ReservationTypeEnum.eateries -> {
                        bindingDialog.notificationContent.text =
                            bindingDialog.root.context.resources.getString(R.string.your) + " " + phoneNotifList.mobileReservation!!.eateryservice!!.title.default + " " + bindingDialog.root.context.resources.getString(
                                R.string.notif_booking_accepted_p2
                            )
                    }

                    ReservationTypeEnum.events -> {
                        bindingDialog.notificationContent.text =
                            bindingDialog.root.context.resources.getString(R.string.your) + " " + phoneNotifList.mobileReservation!!.eventService!!.title.default + " " + bindingDialog.root.context.resources.getString(
                                R.string.notif_booking_accepted_p2
                            )
                    }

                    ReservationTypeEnum.leisure -> {
                        bindingDialog.notificationContent.text =
                            bindingDialog.root.context.resources.getString(R.string.your) + " " + phoneNotifList.mobileReservation!!.leisureService!!.title.default + " " + bindingDialog.root.context.resources.getString(
                                R.string.notif_booking_accepted_p2
                            )
                    }

                    ReservationTypeEnum.meeting -> {
                        bindingDialog.notificationContent.text =
                            bindingDialog.root.context.resources.getString(R.string.your) + " " + phoneNotifList.mobileReservation!!.meetingService!!.title.default + " " + bindingDialog.root.context.resources.getString(
                                R.string.notif_booking_accepted_p2
                            )
                    }

                    ReservationTypeEnum.wellbeingitems -> {
                        bindingDialog.notificationContent.text =
                            bindingDialog.root.context.resources.getString(R.string.your) + " " + bindingDialog.root.context.resources.getString(R.string.well_being) + " " + bindingDialog.root.context.resources.getString(
                                R.string.notif_booking_accepted_p2
                            )
                    }
                    else -> {
                        bindingDialog.notificationContent.text = bindingDialog.root.context.resources.getString(R.string.your) + " " + bindingDialog.root.context.resources.getString(
                                R.string.notif_booking_accepted_p2
                            )
                    }
                }
            }else{
                bindingDialog.notificationContent.text = bindingDialog.root.context.resources.getString(R.string.your) + " " + bindingDialog.root.context.resources.getString(
                    R.string.notif_booking_accepted_p2
                )
            }
        }
        if (!myDialog.isShowing && !this.isFinishing) {
            myDialog.show()
        }
    }

    private fun conciergeReject() {
        val bindingDialog =
            PopupNotifRejectedBinding.inflate(LayoutInflater.from(binding.root.context))
        val contextRef = WeakReference(binding.root.context)
        val myDialog = contextRef.get()?.let { Dialog(it) }
        myDialog?.setContentView(bindingDialog.root)
        myDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.window!!.attributes.gravity = Gravity.TOP

        lifecycleScope.launch(Dispatchers.Main) {
            val dayNameMap = mapOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thrs",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            val phoneNotifListener = PhoneNotifListener(binding.root.context)
            val listNotif = phoneNotifListener.getGuestNotifications(
                ResidenceClient(binding.root.context).getResidenceId().toString()
            ).groupBy { notification ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(notification.createdAt.toString())
                calendar.time = date
                calendar.get(Calendar.DAY_OF_WEEK)

            }.mapKeys { dayNameMap[it.key] }.toList()
            if (listNotif.isNotEmpty()){
                val phoneNotifList = listNotif[listNotif.lastIndex]!!.second[0]
                bindingDialog.notificationContent.text = phoneNotifList.mobileConciergeRequest!!.concierge!!.title.default + " " + bindingDialog.root.context.getString(
                                R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                            )
            }else{
                bindingDialog.notificationContent.text = bindingDialog.root.context.resources.getString(R.string.your) + " " + bindingDialog.root.context.getString(
                    R.string.reservation_has_been_rejected_feel_free_to_contact_our_support_for_more_details
                )
            }
        }
        if (!myDialog.isShowing && !this.isFinishing) {
            myDialog.show()
        }
    }

    private fun conciergeAccept() {
        val bindingDialog =
            PopupNotifAcceptedBinding.inflate(LayoutInflater.from(binding.root.context))
        val contextRef = WeakReference(binding.root.context)
        val myDialog = contextRef.get()?.let { Dialog(it) }
        myDialog?.setContentView(bindingDialog.root)
        myDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.window!!.attributes.gravity = Gravity.TOP

        lifecycleScope.launch(Dispatchers.Main) {
            val dayNameMap = mapOf(
                Calendar.SUNDAY to "Sun",
                Calendar.MONDAY to "Mon",
                Calendar.TUESDAY to "Tue",
                Calendar.WEDNESDAY to "Wed",
                Calendar.THURSDAY to "Thrs",
                Calendar.FRIDAY to "Fri",
                Calendar.SATURDAY to "Sat"
            )
            val phoneNotifListener = PhoneNotifListener(binding.root.context)
            val listNotif = phoneNotifListener.getGuestNotifications(
                ResidenceClient(binding.root.context).getResidenceId().toString()
            ).groupBy { notification ->
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = dateFormat.parse(notification.createdAt.toString())
                calendar.time = date
                calendar.get(Calendar.DAY_OF_WEEK)

            }.mapKeys { dayNameMap[it.key] }.toList()
            if (listNotif.isNotEmpty()){
                val phoneNotifList = listNotif[listNotif.lastIndex]!!.second[0]
                bindingDialog.notificationContent.text =
                    bindingDialog.root.context.resources.getString(R.string.your) + " " + phoneNotifList.mobileConciergeRequest!!.concierge!!.title.default + " " + bindingDialog.root.context.resources.getString(R.string.notif_booking_accepted_p2)
            }else{
                bindingDialog.notificationContent.text = bindingDialog.root.context.resources.getString(R.string.your) + " " + bindingDialog.root.context.resources.getString(
                    R.string.notif_booking_accepted_p2
                )
            }
        }
        if (!myDialog.isShowing && !this.isFinishing) {
        myDialog.show()
        }
    }

    companion object {
        private var textView: TextView? = null
        fun setTextView(textView: TextView) {
            Companion.textView = textView
        }

        fun updateTextView(text: String) {
            textView?.text = text
            textView?.startAnimation(shakeBadge())
        }

        fun shakeBadge(): TranslateAnimation? {
            val shake = TranslateAnimation(0f, 10f, 0f, 0f)
            shake.duration = 5000
            shake.interpolator = CycleInterpolator(7f)
            return shake
        }
    }
}
