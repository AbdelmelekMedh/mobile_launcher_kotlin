package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.Device_byGsfIdQuery

class DeviceClient(context: Context) {
    private val graphQLClient = GraphQLClient(context);

    suspend fun byGsfId(): Device_byGsfIdQuery.Device_byGsfId? {
        val gsfId = graphQLClient.getGsfId()
        Log.e("gsfff",gsfId.toString())
        val response = graphQLClient.client.query(Device_byGsfIdQuery(gsfId.toString())).execute()
        Log.d("error", response.toString())
        return response?.data?.device_byGsfId
    }
}