package com.hellodati.launcher.api

import android.content.Context
import com.hellodati.launcher.Meeting_mobileQuery
import com.hellodati.launcher.WellBeing_mobileQuery
import java.lang.Exception

class MeetingClient(context:Context) {
    private val graphQLClient = GraphQLClient(context)

    suspend fun getMeetings(): List<Meeting_mobileQuery.Meeting_mobile> {
        return try {
            val response = graphQLClient.client.query(Meeting_mobileQuery()).execute()
            response.data!!.meeting_mobile!!
        } catch (e: Exception) {
            emptyList()
        }

    }
}