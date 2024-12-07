package com.hellodati.launcher.api

import android.content.Context
import com.hellodati.launcher.GetMobileReservationsQuery
import java.lang.Exception

class GetReservationsClient(context:Context) {
    private val graphQLClient = GraphQLClient(context)
    suspend fun getAllReservations(residenceId:String): List<GetMobileReservationsQuery.GetMobileReservation> {
        return try {
            val response =
                graphQLClient.client.query(GetMobileReservationsQuery(residenceId))
                    .execute()
            response.data!!.getMobileReservations!!
        } catch (e: Exception) {
            emptyList()
        }

    }
}