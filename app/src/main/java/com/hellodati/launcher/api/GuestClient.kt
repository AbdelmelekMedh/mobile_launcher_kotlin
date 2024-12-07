package com.hellodati.launcher.api

import android.accounts.NetworkErrorException
import android.content.Context
import android.util.Log
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.hellodati.launcher.CheckGuestPasswordQuery
import com.hellodati.launcher.GuestQuery
import com.hellodati.launcher.Guest_initialize_codeMutation
import com.hellodati.launcher.Guest_updateMutation
import com.hellodati.launcher.type.InitializeGuestCodeInput
import com.hellodati.launcher.type.UpdateGuestInput

class GuestClient(context: Context) {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"

    suspend fun initializeCode(input: InitializeGuestCodeInput): Boolean? {
        return try {
            val response = graphQLClient.client.mutation(Guest_initialize_codeMutation(
                input
            )).execute()

            response.data?.guest_initialize_code
        } catch (e: ApolloNetworkException) {
            Log.e(TAG, "Error initialize password: ${e.message}")
            false
        }
    }

    suspend fun updateProfile(input: UpdateGuestInput): Guest_updateMutation.Guest_update {

        val response = graphQLClient.client.mutation(Guest_updateMutation(input)).execute()
        return response.data!!.guest_update

    }

    suspend fun checkPassword(guestId: String, password: String): Boolean? {
        return try {
            val response = graphQLClient.client.query(CheckGuestPasswordQuery(guestId, password)).execute()
            response.data!!.checkGuestPassword
        } catch (e: ApolloNetworkException) {
            Log.e(TAG, "Error checking password: ${e.message}")
            false
        }
    }

    suspend fun getGuestById(guestId:String): GuestQuery.Guest? {
        return try {
            val response = graphQLClient.client.query(GuestQuery(guestId)).execute()
            response.data!!.guest
        } catch (e: Exception) {
            Log.e(TAG, "Error get guest: ${e.message}")
            null
        }
    }
}