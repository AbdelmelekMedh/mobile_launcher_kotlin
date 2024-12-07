package com.hellodati.launcher.api

import android.content.Context
import android.util.Log
import com.hellodati.launcher.Residence_byGsfIdQuery
import com.hellodati.launcher.RoomPhonesQuery
import com.hellodati.launcher.Room_By_ResidenceIdQuery
import com.hellodati.launcher.Room_by_GSFIDQuery
import java.lang.Exception

class RoomPhones(context: Context) {
    private val graphQLClient = GraphQLClient(context)

    private val TAG = "ErrorGraphql"

    suspend fun getAllRooms(): List<RoomPhonesQuery.RoomPhone> {
        return try {
            val response = graphQLClient.client.query(RoomPhonesQuery()).execute()
            response.data!!.roomPhones!!
        } catch (e: Exception) {
            emptyList()
        }

    }

    suspend fun getRoom(residenceId: String): Room_By_ResidenceIdQuery.Room_By_ResidenceId? {
        return try {
            val response = graphQLClient.client.query(Room_By_ResidenceIdQuery(residenceId)).execute()
            response.data?.room_By_ResidenceId
        } catch (e: Exception) {
            Log.e(TAG, "Error getting room by residence id: ${e.message}")
            null
        }
    }

    suspend fun getRoomByGsfId(gsfId: String): Room_by_GSFIDQuery.Room_by_GSFID? {
        return try {
            val response = graphQLClient.client.query(Room_by_GSFIDQuery(gsfId)).execute()
            response.data?.room_by_GSFID
        } catch (e: Exception) {
            Log.e(TAG, "Error getting room by residence id: ${e.message}")
            null
        }
    }
}