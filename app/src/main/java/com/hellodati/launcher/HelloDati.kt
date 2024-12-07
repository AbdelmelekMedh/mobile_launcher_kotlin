package com.hellodati.launcher

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.hellodati.launcher.api.PhoneNotifListener
import com.hellodati.launcher.receiver.NetworkReceiver
import com.hellodati.launcher.ui.activity.QRCodeActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HelloDati : Application() {
    private lateinit var networkReceiver: NetworkReceiver


    override fun onCreate() {
        super.onCreate()
        networkReceiver = NetworkReceiver(applicationContext)
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
        listenToNotifs()
    }

    private fun listenToNotifs() {
        val hotelLinksPreferences = applicationContext.getSharedPreferences("hotel-links", MODE_PRIVATE)
        val api_graphql_server = hotelLinksPreferences.getString("api_graphql_server", "")
        if(api_graphql_server == null)  {
            redirectToQRCode()
            return
        }

        GlobalScope.launch {
            val phoneNotify = PhoneNotifListener(applicationContext)
            phoneNotify.listen()
        }
    }

    private fun redirectToQRCode() {
        val intent = Intent(applicationContext, QRCodeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }
}
