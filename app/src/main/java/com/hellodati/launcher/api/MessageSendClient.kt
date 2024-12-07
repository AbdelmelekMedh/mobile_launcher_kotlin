package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.Cancel_orderMutation
import com.hellodati.launcher.Message_sendMutation
import com.hellodati.launcher.type.CreateMessageInput

class MessageSendClient(context: Context) {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"

    suspend fun sendMessage(input: CreateMessageInput) : Message_sendMutation.Message_send{
        try {
            val response = graphQLClient.client.mutation(Message_sendMutation(input)).execute()
            return response.data!!.message_send
        } catch (e: Exception) {
            Log.e(TAG, "cancelOrder: ${e.message}")
            throw e
        }
    }
}