package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.Order_createMutation
import com.hellodati.launcher.type.CreateOrderInput

class OrderClient(context: Context) {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"
    suspend fun createOrder(input:CreateOrderInput): Order_createMutation.Order_create {
            try {
                val response = graphQLClient.client.mutation(Order_createMutation(input)).execute()
                return response.data!!.order_create!!
            } catch (e: Exception) {
                Log.e(TAG, "Error creating order: ${e.message}")
                throw e
            }
        }
    }
