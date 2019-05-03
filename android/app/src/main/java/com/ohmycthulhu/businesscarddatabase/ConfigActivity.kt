package com.ohmycthulhu.businesscarddatabase

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class ConfigActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sharedPreferences = getSharedPreferences("com.ohmycthulhu.businesscarddatabase", Context.MODE_PRIVATE)

        configIPAddress.setText(sharedPreferences.getString("api_address", "http://192.168.1.8"))

        setResult(Activity.RESULT_CANCELED)

        saveChangesButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            sharedPreferences.edit().putString("api_address", configIPAddress.text.toString()).apply()
            finish()
        }
    }
}