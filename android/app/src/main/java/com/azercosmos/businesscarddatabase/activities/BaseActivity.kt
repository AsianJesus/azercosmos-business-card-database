package com.azercosmos.businesscarddatabase.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.azercosmos.businesscarddatabase.BuildConfig
import com.azercosmos.businesscarddatabase.R
import kotlinx.android.synthetic.main.toolbar_layout.*

open class BaseActivity : AppCompatActivity() {
    open val menuRes: Int = if (BuildConfig.DEBUG) R.menu.menu_common_debug else R.menu.menu_common
    protected val REQUEST_CONFIG = 2

    protected fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.title = ""
        tiLogo.setOnClickListener {
            goToHomePage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(menuRes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when(item.itemId) {
            R.id.action_settings -> {
                openSettings()
                return true
            }
            R.id.action_logout -> {
                logout()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun logout () {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("force", true)
        startActivity(intent)
        finish()
    }

    private fun openSettings () {
        // Toast.makeText(this, "Opening settings", Toast.LENGTH_SHORT).show()
        Intent(this, ConfigActivity::class.java).also {
            startActivityForResult(it, REQUEST_CONFIG)
        }
    }

    private fun goToHomePage () {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}