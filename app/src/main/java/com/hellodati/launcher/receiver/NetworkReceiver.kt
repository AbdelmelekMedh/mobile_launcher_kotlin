package com.hellodati.launcher.receiver

import android.accounts.NetworkErrorException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import android.widget.Toast
import com.hellodati.launcher.api.PhoneNotifListener
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.ui.activity.AuthActivity
import com.hellodati.launcher.ui.activity.MainActivity
import com.hellodati.launcher.ui.activity.QRCodeActivity
import com.hellodati.launcher.ui.activity.VideoActivity
import com.hellodati.launcher.ui.activity.WelcomeActivity
import com.hellodati.launcher.ui.fragment.HomeFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NetworkReceiver(context: Context) : BroadcastReceiver() {
    val applicationContext: Context = context.applicationContext
    private lateinit var hotelLinksPreferences: SharedPreferences

    private fun listenToNotifs() {
        GlobalScope.launch {
            val phoneNotify = PhoneNotifListener(applicationContext)
            phoneNotify.listen()
        }
    }
    override  fun onReceive(context: Context, intent: Intent) {

        hotelLinksPreferences = context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
        val api_graphql_server = hotelLinksPreferences.getString("api_graphql_server", null);

        if(api_graphql_server == null)  {
            redirectToQRCode()
            return
        }

        GlobalScope.launch (Dispatchers.Main) {
            try {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = connectivityManager.activeNetworkInfo
                val residenceClient = ResidenceClient(applicationContext)
                val residence = residenceClient.byGsfId()
                val savedResidenceId = residenceClient.getResidenceId()

                if (networkInfo == null || !networkInfo.isConnected) {

                } else {
                    if(residence?.id != savedResidenceId) {
                        if (residence?.id != null){
                            residenceClient.removeResidenceId()
                            redirectToLanguageSelect()
                        }else {
                            residenceClient.removeResidenceId()
                            redirectToVideo()
                        }
                    }else{
                        redirectToMain()
                    }
                    listenToNotifs()
                }
            }catch ( e: NetworkErrorException) {
                Log.e("nodata",e.message.toString())
            }

        }
    }

    private fun redirectToVideo() {
        val intent = Intent(applicationContext, VideoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    private fun redirectToLanguageSelect() {
        val intent = Intent(applicationContext, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    private fun redirectToMain() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    private fun redirectToQRCode() {
        val intent = Intent(applicationContext, QRCodeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }
}
