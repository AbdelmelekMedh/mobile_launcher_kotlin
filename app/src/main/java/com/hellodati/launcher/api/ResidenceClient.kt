package com.hellodati.launcher.api

import android.accounts.NetworkErrorException
import android.content.Context
import android.util.Log
import com.apollographql.apollo3.exception.ApolloNetworkException
import com.hellodati.launcher.Residence_byGsfIdQuery

class ResidenceClient(context: Context) {
    private val graphQLClient = GraphQLClient(context);
    private val applicationContext: Context = context.applicationContext
    private val residencePref = applicationContext.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
    private val TAG = "ErrorGraphql"

    suspend fun byGsfId(): Residence_byGsfIdQuery.Residence_byGsfId? {
        return try {
            val gsfId = graphQLClient.getGsfId()
            val response = graphQLClient.client.query(Residence_byGsfIdQuery(gsfId.toString())).execute()

            saveHotspotSSID(response.data?.residence_byGsfId?.device?.hotspotSSID)
            saveHotspotPassword(response.data?.residence_byGsfId?.device?.hotspotPassword)
            saveDataLimit(response.data?.residence_byGsfId?.device?.dataLimit.toString())
            saveCallLimit(response.data?.residence_byGsfId?.device?.callLimit.toString())

            response.data?.residence_byGsfId
        } catch (e: ApolloNetworkException) {
            Log.e(TAG, "Error getting residence by GsfId: ${e.message}")
            throw NetworkErrorException()
        }
    }

    fun saveResidenceId(residenceId: String?) {
        residencePref.edit().putString("residence_id", residenceId).apply()
    }

    fun getResidenceId(): String? {
        return residencePref.getString("residence_id", "")
    }

    fun removeResidenceId() {
        residencePref.edit().remove("residence_id").apply()
    }

    fun saveGuestId(guestId: String?) {
        residencePref.edit().putString("guest_id", guestId).apply()
    }
    fun getGuestId(): String? {
        return residencePref.getString("guest_id", "")
    }

    fun saveHotspotSSID(ssid: String?) {
        residencePref.edit().putString("hotspot_ssid", ssid).apply()
    }
    fun getHotspotSSID(): String? {
        return residencePref.getString("hotspot_ssid", "")
    }

    fun saveHotspotPassword(password: String?) {
        residencePref.edit().putString("hotspot_password", password).apply()
    }
    fun getHotspotPassword(): String? {
        return residencePref.getString("hotspot_password", "")
    }

    fun saveDataLimit(dataLimit: String?) {
        residencePref.edit().putString("dataLimit", dataLimit).apply()
    }
    fun getDataLimit(): String? {
        return residencePref.getString("dataLimit", "")
    }

    fun saveCallLimit(callLimit: String?) {
        residencePref.edit().putString("callLimit", callLimit).apply()
    }
    fun getCallLimit(): String? {
        return residencePref.getString("callLimit", "")
    }

    fun saveCSI() {
        residencePref.edit().putBoolean("csi", true).apply()
    }

    fun saveCSIStatus() {
        residencePref.edit().putBoolean("csiStatus", true).apply()
    }

    fun getCSI(): Boolean {
        return residencePref.getBoolean("csi", false)
    }

    fun getCSIStatus(): Boolean {
        return residencePref.getBoolean("csiStatus", false)
    }
}