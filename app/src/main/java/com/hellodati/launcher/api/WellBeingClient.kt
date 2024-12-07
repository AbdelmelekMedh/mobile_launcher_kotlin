package com.hellodati.launcher.api

import android.content.Context
import com.hellodati.launcher.Ird_menuQuery
import com.hellodati.launcher.WellBeing_mobileQuery
import com.hellodati.launcher.type.IrdCategoryEnum
import java.lang.Exception

class WellBeingClient(context:Context) {
    private val graphQLClient = GraphQLClient(context)

    suspend fun getWellBeingMenu(): List<WellBeing_mobileQuery.WellBeing_mobile> {
        return try {
            val response = graphQLClient.client.query(WellBeing_mobileQuery()).execute()
            response.data!!.wellBeing_mobile
        } catch (e: Exception) {
            emptyList()
        }

    }
}