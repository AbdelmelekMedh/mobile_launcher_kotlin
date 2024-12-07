package com.hellodati.launcher.api

import android.content.Context
import com.hellodati.launcher.Events_mobileQuery
import com.hellodati.launcher.Leisures_mobileQuery
import java.lang.Exception

class EventClient(context:Context){
    private val graphQLClient = GraphQLClient(context)

    suspend fun getAllEvent(): List<Events_mobileQuery.Events_mobile> {
        return try {
            val response = graphQLClient.client.query(Events_mobileQuery()).execute()
            response.data!!.events_mobile!!
        } catch (e: Exception) {
            emptyList()
        }

    }
}