package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.Mark_all_as_readMutation

class MarkAllAsReadClient(context: Context)  {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"

    suspend fun MarkAllAsRead(conversationId: String,isFromGuest: Boolean){
         try {
            graphQLClient.client.mutation(Mark_all_as_readMutation(conversationId,isFromGuest)).execute()
        } catch (e: Exception) {
            Log.e(TAG, "Error AllAsRead: ${e.message}")
        }
    }
}