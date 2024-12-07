package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.ClickMutation
import com.hellodati.launcher.Order_createMutation
import com.hellodati.launcher.type.CreateOrderInput
import com.hellodati.launcher.type.ServiceEnum

class ClickClient (context: Context) {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"

    suspend fun createClick(residenceId:String,itemId:String,service:ServiceEnum): ClickMutation.Click? {
        return try {
            val response = graphQLClient.client.mutation(ClickMutation(itemId,residenceId,service)).execute()
            response.data?.click
        } catch (e: Exception) {
            Log.e(TAG, "Error click: ${e.message}")
            null
        }
    }
}