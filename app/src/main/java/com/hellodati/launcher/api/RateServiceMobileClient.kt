package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.RateServiceMobileMutation
import com.hellodati.launcher.type.ServiceEnum

class RateServiceMobileClient(context:Context) {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"
    suspend fun createRate(comment: String, itemId: String, rating : Double, residenceId: String, type:ServiceEnum): RateServiceMobileMutation.RateServiceMobile {
        try {
            val response = graphQLClient.client.mutation(RateServiceMobileMutation(comment,itemId,rating,residenceId,type)).execute()
            return response.data!!.rateServiceMobile
        } catch (e: Exception) {
            Log.e(TAG, "Error creating rate: ${e.message}")
            throw e
        }
    }
}