package com.ohmycthulhu.businesscarddatabase

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity;
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SimpleAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.request.JsonArrayRequest
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity(), BusinessCardController {

    /*
        Codes for activity results
     */
    private val REQUEST_PERMISSIONS = 1
    private val REQUEST_CONFIG = 2
    private val REQUEST_NEW_CARD = 3
    private val REQUEST_EDIT_CARD = 4

    lateinit var requestQueue: RequestQueue
    lateinit var sharedPreferences: SharedPreferences

    /*
        These variables needed for editing cards
        afterEditCallback is called after EditCard activity ends up with Result OK
     */
    var afterCardEditCallback: AfterCardEditCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setTitle("")

        sharedPreferences = getSharedPreferences("com.ohmycthulhu.businesscarddatabase", Context.MODE_PRIVATE)

        requestQueue = Volley.newRequestQueue(this)

        fab.setOnClickListener { view ->
            Intent(this, NewCardActivity::class.java).also {
                startActivityForResult(it, REQUEST_NEW_CARD)
            }
        }


        loadCards()
        /*cardsList.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, "It works! You selected ${cards[position].name}", Toast.LENGTH_SHORT).show()
            Intent(this, ShowCardActivity::class.java).also { intent ->
                val extras = Bundle()
                extras.putString("name", cards[position].name)
                intent.putExtras(extras)
                intent.putExtra("card", cards[position])
                startActivityForResult(intent, REQUEST_SHOW_CARD)
            }
        }*/
        requestPermissions()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
    }

    private fun loadCards () {
        requestQueue.cancelAll("load_cards")
        val request = JsonArrayRequest(Request.Method.GET,
            "${sharedPreferences.getString("api_address", "http://192.168.1.8")}/business-cards",
            null,
            Response.Listener {
                val userID = sharedPreferences.getInt("user_id", 1)
                val cards: ArrayList<BusinessCard> = ArrayList(it.length())
                for (i in 0 until it.length()) {
                    val obj = it[i] as JSONObject
                    val note = if(obj.has("note")) obj.getString("note") else ""
                    val imagePath = obj.getString("image_path")

                    cards.add(BusinessCard(obj.getInt("id"), obj.getString("name"), obj.getString("company_name"),
                        obj.getString("email"), obj.getString("address"), obj.getString("mobile"),
                        obj.getString("website"), obj.getString("position"),
                        obj.getInt("private") == 1, note, imagePath, obj.getInt("created_by") == userID))
                }
                showCards(cards)
            },
            Response.ErrorListener {
                Toast.makeText(this, "Error occurred on loading cards: ${it.message}", Toast.LENGTH_LONG).show()
            })
        request.tag = "load_cards"
        request.setShouldCache(false)
        requestQueue.add(request)
    }

    private fun openSettings () {
        Toast.makeText(this, "Opening settings", Toast.LENGTH_SHORT).show()
        Intent(this, ConfigActivity::class.java).also {
            startActivityForResult(it, REQUEST_CONFIG)
        }
    }

    override fun deleteCard(id: Int, afterCardDeleteCallback: AfterCardDeleteCallback) {
        val dialog = DeleteCardDialog()
        dialog.setCallback {
            if (it) {
                Toast.makeText(this, "Card $id is deleted", Toast.LENGTH_SHORT).show()
                sendDeleteRequest(id) {
                    afterCardDeleteCallback.afterDelete(id)
                }
            }
        }
        dialog.show(supportFragmentManager, "delete_card")
    }

    override fun editCard(card: BusinessCard, afterCardEditCallback: AfterCardEditCallback) {
        Toast.makeText(this, "Card ${card.id} is edited", Toast.LENGTH_SHORT).show()
        this.afterCardEditCallback = afterCardEditCallback
        Intent(this, EditCardActivity::class.java).also {
            it.putExtra("card", card)
            startActivityForResult(it, REQUEST_EDIT_CARD)
        }
    }

    override fun showImage(card: BusinessCard) {
        /*
            Open modal window with current image
         */
        val url = "${sharedPreferences.getString("api_address", "http://192.168.1.8")}/${card.imagePath}"
        Toast.makeText(this, "Image is at $url", Toast.LENGTH_SHORT).show()
        val modal = ShowImageModal()
        modal.setImageURL(url)
        modal.dialog// .window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        modal.show(supportFragmentManager, "show_image")
    }

    private fun sendDeleteRequest(id: Int, callback: () -> Unit) {
        val url = "${sharedPreferences.getString("api_address", "http://192.168.1.8")}/business-cards/$id"
        val request = StringRequest(Request.Method.DELETE, url, {
            Toast.makeText(this, "We deleted card #$id!", Toast.LENGTH_SHORT).show()
            callback()
        }, {
            Toast.makeText(this, "Couldn't delete the card", Toast.LENGTH_SHORT).show()
            Log.e("delete error", it.message)
        })

        request.tag = "delete_card"
        requestQueue.add(request)
    }

    private fun showCards (cards: ArrayList<BusinessCard>) {
        val adapter = BusinessCardsAdapter(cards, this, this)
        cardsList.setAdapter(adapter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CONFIG) {
            if (resultCode == Activity.RESULT_OK) {
                loadCards()
            }
        }
        if (requestCode == REQUEST_NEW_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                loadCards()
            }
        }
        if (requestCode == REQUEST_EDIT_CARD) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val card = data.getSerializableExtra("new_card") as BusinessCard
                if (afterCardEditCallback == null) {
                    Log.d("aftercard", "Callback is null")
                } else {
                    Log.d("aftercard", "Callback is not null")
                }
                afterCardEditCallback?.afterEdit(card)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        cardsList.setIndicatorBounds(cardsList.right - 200, cardsList.width)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
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
            R.id.action_refresh -> {
                loadCards()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
