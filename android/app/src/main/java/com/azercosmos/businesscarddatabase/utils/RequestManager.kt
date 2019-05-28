package com.azercosmos.businesscarddatabase.utils

import android.content.SharedPreferences
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.azercosmos.businesscarddatabase.BuildConfig

class RequestManager {
    companion object {
        private var requestQueue: RequestQueue? = null
        private var sharedPreferences: SharedPreferences? = null

        private val DEFAULT_ADDRESS = "http://192.168.1.8"
        private val API_ADDRESS = "https://bcd.labproxy.com/azercosmos-business-card-database/api/public"

        fun setSharedPreferences (sharedPreferences: SharedPreferences) {
            this.sharedPreferences = sharedPreferences
        }

        fun setRequestQueue (requestQueue: RequestQueue) {
            this.requestQueue = requestQueue
        }

        fun <T> fillHeaders (request: Request<T>): Request<T> {
            // val headers = request.headers
            val headers: MutableMap<String, String> = mutableMapOf()

            headers.put("Authorization", sharedPreferences?.getString("token", "") as String)
            request.headers = headers
            return request
        }

        fun <T> sendRequest(request: Request<T>) {
            requestQueue?.add(fillHeaders(request))
        }

        fun getServerUrl(): String {
            return if(BuildConfig.DEBUG) sharedPreferences?.getString("api_address", DEFAULT_ADDRESS) ?: DEFAULT_ADDRESS
                    else API_ADDRESS
        }
    }
}