package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.Cancel_orderMutation


class CancelOrderClient (context: Context) {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"

    suspend fun cancelOrder(orderid:String): Cancel_orderMutation.Cancel_order {
        try {
            val response = graphQLClient.client.mutation(Cancel_orderMutation(orderid)).execute()
            return response.data!!.cancel_order!!
        } catch (e: Exception) {
            Log.e(TAG, "cancelOrder: ${e.message}")
            throw e
        }
    }
}