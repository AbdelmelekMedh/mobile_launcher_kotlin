package com.hellodati.launcher.providers

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.hellodati.launcher.api.ResidenceClient

class HotspotCredentialsProvider : ContentProvider() {
    val AUTHORITY = "com.hellodati.launcher.provider"
    val CONTENT_URI = Uri.parse("content://$AUTHORITY/data")

    private val COLUMN_SSID = "ssid"
    private val COLUMN_PASSWORD = "password"
    private val COLUMN_DATA_LIMIT = "dataLimit"
    private val COLUMN_CALL_LIMIT = "callLimit"

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(): Boolean {
        context?.let {
            sharedPreferences = it.getSharedPreferences("user_infos", Context.MODE_PRIVATE)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = MatrixCursor(arrayOf(COLUMN_SSID, COLUMN_PASSWORD, COLUMN_DATA_LIMIT, COLUMN_CALL_LIMIT))
        val ssid = sharedPreferences.getString("hotspot_ssid", "---")
        val password = sharedPreferences.getString("hotspot_password", "---")
        val dataLimit = sharedPreferences.getString("dataLimit", "0")
        val callLimit = sharedPreferences.getString("callLimit", "0")
        cursor.addRow(arrayOf(ssid, password, dataLimit, callLimit))
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.dir/vnd.com.hellodati.launcher.provider.data"
    }
}
