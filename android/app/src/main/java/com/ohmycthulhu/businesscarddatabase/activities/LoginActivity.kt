package com.ohmycthulhu.businesscarddatabase.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.request.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ohmycthulhu.businesscarddatabase.utils.RequestManager
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import com.ohmycthulhu.businesscarddatabase.R
import com.ohmycthulhu.businesscarddatabase.utils.AssetsDecompressor

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private val REQUEST_MAIN_ACTIVITY = 1512
    private val REQUEST_PERMISSIONS = 1215

    enum class ACTION(private val code: Int) {
        ACTION_FINISH(1),
        ACTION_LOGOUT(2);

        fun getCode (): Int {
            return code
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("com.ohmycthulhu.businesscarddatabase", Context.MODE_PRIVATE)
        val requestQueue = Volley.newRequestQueue(this)

        setContentView(R.layout.activity_login)

        AssetsDecompressor.requestPermissions(this, REQUEST_PERMISSIONS)
        val assetsPath = "${Environment.getExternalStorageDirectory()}/${getString(R.string.assets_path)}"
        AssetsDecompressor.unpack(baseContext.assets, assetsPath, "tessdata")

        RequestManager.setSharedPreferences(sharedPreferences)
        RequestManager.setRequestQueue(requestQueue)

        loginButton.setOnClickListener {
            tryLogin(loginLogin.text.toString(), loginPassword.text.toString()) {id: Int, token: String ->
                sharedPreferences.edit()
                        .putInt("user_id", id)
                        .putBoolean("logged", true)
                        .putString("token", token).apply()
                redirectToMain()
            }
        }

        loginConfigure.setOnClickListener {
            Intent(this, ConfigActivity::class.java).also {
                startActivity(it)
            }

        }

        val isLogged = sharedPreferences.getBoolean("logged", false)
        if (isLogged) {
            redirectToMain()
        }

    }

    private fun tryLogin (username: String, password: String,  callback: (id: Int, token: String) -> Unit) {
        val url = "${RequestManager.getServerUrl()}/users"
        val params = JSONObject()
        try {
            params.put("login", username)
            params.put("password", password)
        } catch (e: Exception) {
            throw e
        }
        val request = JsonObjectRequest(Request.Method.POST, url, params, {
            callback(it.getInt("id"), it.getString("key"))
        }, {
            // Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        })
        RequestManager.sendRequest(request)
    }

    private fun redirectToMain() {
        Intent(this, MainActivity::class.java).also {
            startActivityForResult(it, REQUEST_MAIN_ACTIVITY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_MAIN_ACTIVITY) {
            if (resultCode == ACTION.ACTION_LOGOUT.getCode()) {
                sharedPreferences.edit().putBoolean("logged", false).apply()
            }
            if (resultCode == ACTION.ACTION_FINISH.getCode()) {
                finish()
            }
        }
    }
}