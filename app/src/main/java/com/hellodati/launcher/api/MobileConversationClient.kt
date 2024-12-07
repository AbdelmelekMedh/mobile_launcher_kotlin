package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.Mobile_conversationQuery
import java.lang.Exception

class MobileConversationClient(context: Context) {
    private val graphQLClient = GraphQLClient(context)

    suspend fun getConversation(guestId: String): Mobile_conversationQuery.Mobile_conversation? {
        return try {
            val response = graphQLClient.client.query(Mobile_conversationQuery(guestId)).execute()
            response.data?.mobile_conversation
        }catch (e: Exception){
            Log.e("ErrorGetConversation", "Error getting Conversation: ${e.message}")
            null
        }
    }
}