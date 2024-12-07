package com.hellodati.launcher.ui.activity

import android.content.Context
import android.content.IntentFilter
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hellodati.launcher.R
import com.hellodati.launcher.databinding.ActivityConnectionBinding
import com.hellodati.launcher.databinding.ActivityMainBinding
import com.hellodati.launcher.databinding.NavHeaderMainBinding
import com.hellodati.launcher.receiver.InitNetworkReciever

class ConnectionActivity : AppCompatActivity() {
    private lateinit var initNetworkReciever: InitNetworkReciever
    private lateinit var binding: ActivityConnectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val connectivityManager =
            binding.root.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        supportActionBar?.hide()
        if (networkInfo == null || !networkInfo.isConnected) {
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
        } else {

            initNetworkReciever = InitNetworkReciever(applicationContext, this)
            val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(initNetworkReciever, intentFilter)

            val loadingSpinner = binding.loadingSpinner

            // Show the loading spinner
            loadingSpinner.visibility = View.VISIBLE
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(initNetworkReciever)
    }
}
