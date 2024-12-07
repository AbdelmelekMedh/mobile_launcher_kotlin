package com.hellodati.launcher.api

import android.accounts.NetworkErrorException
import android.content.Context
import android.util.Log
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.hellodati.launcher.CreateConciergeRequestMutation
import com.hellodati.launcher.Reservation_createMutation
import com.hellodati.launcher.type.CreateConciergeRequestInput
import com.hellodati.launcher.type.CreateReservationInput

class ReservationClient(context:Context) {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"

    suspend fun createReservation(input: CreateReservationInput): Reservation_createMutation.Reservation_create {
        try {
            val response = graphQLClient.client.mutation(Reservation_createMutation(input)).execute()
            return response.data!!.reservation_create
        } catch (e: ApolloNetworkException) {
            Log.e(TAG, "Error creating reservation: ${e.message}")
            throw NetworkErrorException()
        }
    }

    suspend fun createReservationConcierge(input: CreateConciergeRequestInput): CreateConciergeRequestMutation.Data? {
        try {
            val response = graphQLClient.client.mutation(CreateConciergeRequestMutation(input)).execute()
            return response.data

        } catch (e: ApolloNetworkException) {
            Log.e(TAG, "Error creating reservation: ${e.message}")
            throw NetworkErrorException()
        }
    }
}