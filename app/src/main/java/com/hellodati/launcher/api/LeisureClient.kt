package com.hellodati.launcher.api

import android.content.Context
import com.hellodati.launcher.Leisures_mobileQuery
import com.hellodati.launcher.WellBeing_mobileQuery
import java.lang.Exception

class LeisureClient(context:Context) {
    private val graphQLClient = GraphQLClient(context)

    suspend fun getLeisureMenu(): List<Leisures_mobileQuery.Leisures_mobile> {
        return try {
            val response = graphQLClient.client.query(Leisures_mobileQuery()).execute()
            response.data!!.leisures_mobile!!
        } catch (e: Exception) {
            emptyList()
        }

    }
}