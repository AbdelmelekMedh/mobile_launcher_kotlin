package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.Conversation_createMutation
import com.hellodati.launcher.type.CreateConversationInput

class ConversationCreateClient (context: Context) {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"
    suspend fun createConversation(input: CreateConversationInput): Conversation_createMutation.Conversation_create{
        try {
            val response = graphQLClient.client.mutation(Conversation_createMutation(input, true)).execute()
            //Log.e(TAG, response.data!!.conversation_create.toString())
            return response.data!!.conversation_create
        } catch (e: Exception) {
            Log.e(TAG, "Error conversation: ${e.message}")
            throw e
        }
    }
}