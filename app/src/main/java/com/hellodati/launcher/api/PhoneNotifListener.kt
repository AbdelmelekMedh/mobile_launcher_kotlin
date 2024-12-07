package com.hellodati.launcher.api

import android.content.Context
import android.content.Intent
import android.util.Log
import com.apollographql.apollo3.exception.ApolloException
import com.hellodati.launcher.ui.activity.AuthActivity
import com.hellodati.launcher.ui.activity.MainActivity
import com.hellodati.launcher.PhoneNotifSubscription
import com.hellodati.launcher.Phone_notif_listQuery
import com.hellodati.launcher.ui.activity.VideoActivity
import com.hellodati.launcher.type.PhoneNotifTypeEnum
import com.hellodati.launcher.ui.activity.GuideActivity
import com.hellodati.launcher.ui.activity.WelcomeActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.retryWhen
import java.lang.Exception
import kotlin.math.pow

class PhoneNotifListener(context: Context) {
    private val graphQLClient = GraphQLClient(context);
    private val applicationContext: Context = context.applicationContext
    suspend fun listen() {
        val gsfId = graphQLClient.getGsfId()
        try {
            graphQLClient.client.subscription(PhoneNotifSubscription(gsfId = gsfId.toString()))
                .toFlow()
                .retryWhen { e, attempt ->
                    delay(2.0.pow(attempt.toDouble()).toLong())
                    true
                }
                .collect {
                    when (it.data?.phoneNotif?.type) {
                        PhoneNotifTypeEnum.check_in -> checkIn()
                        PhoneNotifTypeEnum.check_out -> checkOut()
                        PhoneNotifTypeEnum.reset_passcode -> redirectToAuth()
                        else -> {}
                    }
                }
        } catch (e: ApolloException) {
            Log.e("phone_notif", e.toString())
        }
    }

    private fun checkIn() {
        try {
            ResidenceClient(applicationContext).removeResidenceId()
            ResidenceClient(applicationContext).saveCSI()
            redirectToWelcome()
        } catch (e: Exception) {
            Log.e("phone_notif", e.message.toString())
        }
    }

    private fun checkOut() {
        try {
            ResidenceClient(applicationContext).removeResidenceId()
            redirectToVideo()
        } catch (e: Exception) {
            Log.e("phone_notif", e.message.toString())
        }
    }

    private fun redirectToMain() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    private fun redirectToVideo() {
        val intent = Intent(applicationContext, VideoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    private fun redirectToAuth() {
        ResidenceClient(applicationContext).removeResidenceId()
        val intent = Intent(applicationContext, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    private fun redirectToGuide() {
        val intent = Intent(applicationContext, GuideActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    private fun redirectToWelcome() {
        val intent = Intent(applicationContext, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

    suspend fun getGuestNotifications(residenceId: String): List<Phone_notif_listQuery.Phone_notif_list> {
        return try {
            val response = graphQLClient.client.query(Phone_notif_listQuery(residenceId)).execute()
            response.data!!.phone_notif_list!!
        } catch (e: Exception) {
            emptyList()
        }

    }
}