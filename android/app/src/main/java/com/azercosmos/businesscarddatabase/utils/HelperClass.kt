package com.azercosmos.businesscarddatabase.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import android.widget.Toast
import org.json.JSONObject

class HelperClass {
    companion object {
        private val emailRegex = Regex(".+@.+\\..+")
        private val websiteRegex = Regex(".+\\..+")
        fun validate (name: String, company: String, position: String, email: String, website: String): String? {
            if (name.isEmpty()) {
                return "Name"
            }

            if (company.isEmpty()) {
                return "Company name"
            }

            if (position.isEmpty()) {
                return "Position"
            }

            if (email.isNotEmpty() && !emailRegex.matches(email)) {
                return "Email"
            }

            if (website.isNotEmpty() && !websiteRegex.matches(website)) {
                return "Website"
            }

            return null
        }

        fun filtersToQuery (filters: Map<String, String>): String {
            var result = ""
            filters.entries.map {
                result += "filters[${it.key}]=%25${it.value}%25"
            }.joinToString("&")
            return result
        }

        fun isThereConnection (context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            try {
                return cm.activeNetworkInfo?.isConnected ?: false
            } catch (e: Exception) {
                return false
            }
        }


        fun getPath(context: Context, contentUri: Uri): String {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val loader = CursorLoader(context.applicationContext, contentUri, proj, null, null, null)
            val cursor = loader.loadInBackground()
            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val result = cursor?.getString(column_index as Int)
            cursor?.close()
            return result as String
        }
    }
}