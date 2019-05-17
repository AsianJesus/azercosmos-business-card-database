package com.ohmycthulhu.businesscarddatabase.utils

import android.content.SharedPreferences
import com.android.volley.Request
import com.android.volley.RequestQueue

class RequestManager {
    companion object {
        private lateinit var requestQueue: RequestQueue
        private lateinit var sharedPreferences: SharedPreferences

        fun setSharedPreferences (sharedPreferences: SharedPreferences) {
            this.sharedPreferences = sharedPreferences
        }

        fun setRequestQueue (requestQueue: RequestQueue) {
            this.requestQueue = requestQueue
        }

        fun getSharedPreferences(): SharedPreferences {
            return this.sharedPreferences
        }

        fun getRequestQueue(): RequestQueue {
            return this.requestQueue
        }

        fun <T> fillHeaders (request: Request<T>): Request<T> {
            // val headers = request.headers
            val headers: MutableMap<String, String> = mutableMapOf()

            headers.put("Authorization", sharedPreferences.getString("token", "") as String)
            request.headers = headers
            return request
        }

        fun <T> sendRequest(request: Request<T>) {
            requestQueue.add(fillHeaders(request))
        }
    }
}