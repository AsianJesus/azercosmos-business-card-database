package com.azercosmos.businesscarddatabase.utils

import android.widget.Toast

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
    }
}