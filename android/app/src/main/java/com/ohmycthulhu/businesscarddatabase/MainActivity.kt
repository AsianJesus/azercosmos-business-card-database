package com.ohmycthulhu.businesscarddatabase

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.SimpleAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.request.JsonArrayRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val REQUEST_PERMISSIONS = 1
    val REQUEST_CONFIG = 2
    val REQUEST_NEW_CARD = 3
    val REQUEST_SHOW_CARD = 4
    lateinit var requestQueue: RequestQueue
    var cards: ArrayList<BusinessCard> = arrayListOf()
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        sharedPreferences = getSharedPreferences("com.ohmycthulhu.businesscarddatabase", Context.MODE_PRIVATE)

        requestQueue = Volley.newRequestQueue(this)

        fab.setOnClickListener { view ->
            Intent(this, NewCardActivity::class.java).also {
                startActivityForResult(it, REQUEST_NEW_CARD)
            }
        }


        loadCards()
        cardsList.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, "It works! You selected ${cards[position].name}", Toast.LENGTH_SHORT).show()
            Intent(this, ShowCardActivity::class.java).also { intent ->
                val extras = Bundle()
                extras.putString("name", cards[position].name)
                intent.putExtras(extras)
                intent.putExtra("card", cards[position])
                startActivityForResult(intent, REQUEST_SHOW_CARD)
            }

        }
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
                cards = ArrayList(it.length())
                for (i in 0..it.length()-1) {
                    val obj = it[i] as JSONObject
                    val note = if(obj.has("note")) obj.getString("note") else ""
                    val imagePath = obj.getString("image_path")

                    cards.add(BusinessCard(obj.getInt("id"), obj.getString("name"), obj.getString("company_name"),
                        obj.getString("email"), obj.getString("address"), obj.getString("mobile"),
                        obj.getString("website"), obj.getString("position"),
                        obj.getInt("private") == 1, note, imagePath, obj.getInt("created_by") == userID))
                }
                showCards()
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

    private fun showCards () {
        val adapter = BusinessCardsAdapter(cards, this)
        cardsList.adapter = adapter
        /*cardsList.setOnItemClickListener { parent, view, position, id ->
            val cardModel = cards[position]


        }*/
        /*val elements = ArrayList<HashMap<String, String>>()

        for (card in cards) {
            val hash = HashMap<String, String>()
            hash.put("name", card.name)
            hash.put("company", "${card.company ?: ""} - ${card.position ?: ""}")
            elements.add(hash)
        }

        val to = intArrayOf(R.id.listItemName, R.id.listItemCompany)
        val adapter = SimpleAdapter(this, elements, R.layout.list_item, arrayOf("name", "company"), to)
        cardsList.adapter = adapter*/
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
        if (requestCode == REQUEST_SHOW_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if(data.hasExtra("cardToEdit")) {
                        val adapter = cardsList.adapter as BusinessCardsAdapter
                        adapter.editCard(data.getSerializableExtra("cardToEdit") as BusinessCard)
                    }
                    if(data.hasExtra("cardToDelete")) {
                        val adapter = cardsList.adapter as BusinessCardsAdapter
                        adapter.deleteCard(data.getIntExtra("cardToDelete", 0))
                    }

                }
            }
        }
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
