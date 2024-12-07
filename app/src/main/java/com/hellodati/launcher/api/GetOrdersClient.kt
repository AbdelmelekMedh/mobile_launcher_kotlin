package com.hellodati.launcher.api

import android.content.Context
import com.hellodati.launcher.GetMobileOrdersQuery
import java.lang.Exception

class GetOrdersClient(context: Context) {
    private val graphQLClient = GraphQLClient(context)
    suspend fun getAllOrders(residenceId:String): List<GetMobileOrdersQuery.GetMobileOrder> {
        return try {
            val response =
                graphQLClient.client.query(GetMobileOrdersQuery(residenceId))
                    .execute()
            response.data!!.getMobileOrders!!
        } catch (e: Exception) {
            emptyList()
        }

    }
}