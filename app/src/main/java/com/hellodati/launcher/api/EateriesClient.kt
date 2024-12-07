package com.hellodati.launcher.api

import android.content.Context
import com.hellodati.launcher.EateriesMobileQuery
import java.lang.Exception

class EateriesClient(context:Context) {
    private val graphQLClient = GraphQLClient(context)

    suspend fun getAllEateries(): List<EateriesMobileQuery.EateriesMobile> {
        return try {
            val response = graphQLClient.client.query(EateriesMobileQuery()).execute()
            response.data!!.eateriesMobile!!
        } catch (e: Exception) {
            emptyList()
        }

    }
}