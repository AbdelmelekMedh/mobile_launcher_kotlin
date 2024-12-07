package com.hellodati.launcher.api

import android.content.Context
import android.net.Uri
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.exception.ApolloHttpException
import com.apollographql.apollo3.network.okHttpClient
import com.hellodati.launcher.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.Locale

class GraphQLClient (context: Context) {
    private val hotelLinksPreferences = context.getSharedPreferences("hotel-links", Context.MODE_PRIVATE)
    private val api_socket_server = hotelLinksPreferences.getString("api_socket_server", "")
    private val api_graphql_server = hotelLinksPreferences.getString("api_graphql_server", "")
    private val gServicesUri: Uri = Uri.parse("content://com.google.android.gsf.gservices")
    private val webSocketServerUrl: Uri = Uri.parse(api_socket_server)
    private val serverUrl: Uri = Uri.parse(api_graphql_server)
    private val applicationContext: Context = context.applicationContext
    private val okHttpClient:OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor(context))
        .build()

    val client: ApolloClient = ApolloClient.Builder()
        .serverUrl(serverUrl.toString())
        .webSocketServerUrl(webSocketServerUrl.toString())
        .okHttpClient(okHttpClient)
        .build()

    fun getGsfId(): String? {
        return try {
            val query =
                applicationContext.contentResolver.query(gServicesUri, null, null, arrayOf("android_id"), null)
                    ?: return "Not found"
            if (!query.moveToFirst() || query.columnCount < 2) {
                query.close()
                return "Not found"
            }
            val toHexString = java.lang.Long.toHexString(query.getString(1).toLong())
            query.close()
            toHexString.uppercase(Locale.getDefault()).trim { it <= ' ' }
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        } catch (e2: Exception) {
            e2.printStackTrace()
            null
        }
    }
}
class HeaderInterceptor(private var context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val graphQLClient = GraphQLClient(context)
        val originalRequest: Request = chain.request()
        val modifiedRequest: Request = originalRequest.newBuilder()
            .header("gsfid", graphQLClient.getGsfId().toString())
            .header("residenceid", ResidenceClient(context).getResidenceId().toString())
            .build()
        return chain.proceed(modifiedRequest)
    }
}