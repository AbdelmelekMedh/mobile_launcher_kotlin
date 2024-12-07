package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.SubmitReviewMutation
import com.hellodati.launcher.type.SubmitReviewInput

class GsiSubmitReviewClient(context: Context) {
    private val graphQLClient = GraphQLClient(context)
    private val TAG = "ErrorGraphql"

    suspend fun createGsiReview(input: SubmitReviewInput): Boolean?{
        try {
            val response = graphQLClient.client.mutation(SubmitReviewMutation(input)).execute()
            return response.data!!.submitReview
        } catch (e: Exception) {
            Log.e(TAG, "Error creating rate: ${e.message}")
            throw e
        }
    }
}