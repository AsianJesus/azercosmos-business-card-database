package com.azercosmos.businesscarddatabase.activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.azercosmos.businesscarddatabase.R
import com.azercosmos.businesscarddatabase.utils.RequestManager
import kotlinx.android.synthetic.main.activity_settings.*

class ConfigActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        sharedPreferences = getSharedPreferences("com.azercosmos.businesscarddatabase", Context.MODE_PRIVATE)

        configIPAddress.setText(sharedPreferences.getString("api_address", RequestManager.getServerUrl()))
        configLoginAddress.setText(sharedPreferences.getString("login_address", RequestManager.getLoginUrl()))
        configUserID.setText(sharedPreferences.getInt("user_id", 1).toString())

        setResult(Activity.RESULT_CANCELED)

        saveChangesButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            sharedPreferences.edit().putString("api_address", configIPAddress.text.toString())
                .putString("login_address", configLoginAddress.text.toString())
                .putInt("user_id",  configUserID.text.toString().toInt()).apply()
            finish()
        }
    }
}