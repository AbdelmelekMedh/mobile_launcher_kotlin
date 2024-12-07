package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.ConciergeList_mobileQuery
import com.hellodati.launcher.Events_mobileQuery
import java.lang.Exception

class ConciergeClient(context:Context) {
    private val graphQLClient = GraphQLClient(context)

    suspend fun getConciergeList(): List<ConciergeList_mobileQuery.ConciergeList_mobile> {
        return try {
            val response = graphQLClient.client.query(ConciergeList_mobileQuery()).execute()
            response.data!!.conciergeList_mobile!!
        } catch (e: Exception) {
            emptyList()
        }

    }
}