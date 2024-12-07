package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.GsiAvailableQuestionsQuery

class GsiAvailableQuestionsClient (context: Context){
    private val graphQLClient = GraphQLClient(context)

    suspend fun getAvailableGsiQuestions(residenceId : String): List<GsiAvailableQuestionsQuery.GsiAvailableQuestion>? {
        return try {
            val response = graphQLClient.client.query(GsiAvailableQuestionsQuery(residenceId)).execute()
            response.data?.gsiAvailableQuestions
        }catch (e:Exception){
            Log.e("gsi", e.message.toString())
            null
        }
    }
}