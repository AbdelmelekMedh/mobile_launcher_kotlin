package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.GetGlobalSettingQuery
import java.lang.Exception

class GlobalSettingsClient (context: Context){
    private val graphQLClient = GraphQLClient(context)
    suspend fun getSettings(): GetGlobalSettingQuery.GetGlobalSetting? {

        try {
            val response = graphQLClient.client.query(GetGlobalSettingQuery()).execute()
            return response.data!!.getGlobalSetting!!
        }catch (e:Exception){
            Log.e("TAGK", e.message.toString())
            return null
        }

    }
}