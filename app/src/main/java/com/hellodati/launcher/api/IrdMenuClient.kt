package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.Ird_menuQuery
import com.hellodati.launcher.type.IrdCategoryEnum
import okhttp3.internal.immutableListOf
import java.lang.Exception

class IrdMenuClient(context: Context) {
    private val graphQLClient = GraphQLClient(context)

    suspend fun getAll(type:IrdCategoryEnum): List<Ird_menuQuery.Ird_menu>? {
        return try {
            val response = graphQLClient.client.query(Ird_menuQuery(type)).execute()
            response.data!!.ird_menu
        } catch (e: Exception) {
            emptyList()
        }

    }
}