package com.hellodati.launcher.receiver

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hellodati.launcher.Device_byGsfIdQuery
import com.hellodati.launcher.Residence_byGsfIdQuery
import com.hellodati.launcher.api.DeviceClient
import com.hellodati.launcher.api.ResidenceClient
import com.hellodati.launcher.ui.activity.MainActivity
import com.hellodati.launcher.ui.activity.QRCodeActivity
import com.hellodati.launcher.ui.activity.VideoActivity
import com.hellodati.launcher.ui.activity.WelcomeActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception

class InitNetworkReciever(context: Context, activity: AppCompatActivity) : BroadcastReceiver() {
    private val applicationContext: Context = context.applicationContext
    private val parentActivity: AppCompatActivity = activity;
    private lateinit var hotelLinksPreferences: SharedPreferences

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        hotelLinksPreferences = context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)

        val api_graphql_server = hotelLinksPreferences.getString("api_graphql_server", null);

        if(api_graphql_server == null)  {
            redirectToQRCode()
            return
        }

        if (networkInfo == null || !networkInfo.isConnected) {
            val storedResidenceId: String? = ResidenceClient(applicationContext).getResidenceId()
            if (storedResidenceId == ""){
                redirectToVideo()
            }else{
                redirectToMain()
            }
        } else {
            initDevice()
        }
    }

    private fun redirectToQRCode() {
        val intent = Intent(applicationContext, QRCodeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    private fun initDevice() {
        GlobalScope.launch {
            try {
                val deviceClient = DeviceClient(applicationContext)
                val device: Device_byGsfIdQuery.Device_byGsfId? = deviceClient.byGsfId();

                device?.let { value ->
                    val residenceClient = ResidenceClient(applicationContext)
                    val residence: Residence_byGsfIdQuery.Residence_byGsfId? = residenceClient.byGsfId();

                    if(residence != null) {
                        val storedResidenceId: String? = ResidenceClient(applicationContext).getResidenceId()

                        if(residence?.id == storedResidenceId) {
                            redirectToMain()
                        } else{
                            residenceClient.removeResidenceId()
                            redirectToLanguageSelect()
                        }
                    } else {
                        residenceClient.removeResidenceId()
                        redirectToVideo()
                    }
                }
            } catch (e: Exception) {
                Log.d("error", "url is not valid")
            }
        }
    }

    private fun redirectToVideo() {
        val intent = Intent(applicationContext, VideoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
        parentActivity.finish()
    }

    private fun redirectToLanguageSelect() {
        val intent = Intent(applicationContext, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
        parentActivity.finish()
    }

    private fun redirectToMain() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
        parentActivity.finish()
    }
}