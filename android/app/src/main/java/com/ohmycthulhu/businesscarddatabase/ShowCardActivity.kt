package com.ohmycthulhu.businesscarddatabase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_show_card.*

class ShowCardActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var card: BusinessCard
    private var wasEdited: Boolean = false
    private var requestQueue: RequestQueue? = null
    private val REQUEST_EDIT_CARD  = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_card)

        sharedPreferences = getSharedPreferences("com.ohmycthulhu.businesscarddatabase", Context.MODE_PRIVATE)

        val card = intent.getSerializableExtra("card") as BusinessCard?

        if (card == null) {
            Toast.makeText(this, "Card is null", Toast.LENGTH_SHORT).show()
        } else {
            setFields(card)
        }
    }

    private fun setFields (card: BusinessCard) {
        this.card = card
        showCardName.text = card.name
        showCardCompany.text = card.company ?: ""
        showCardPosition.text = card.position ?: ""
        showCardAddress.text = card.address ?: ""
        showCardPosition.text = card.website ?: ""
        showCardPhone.text = card.phone ?: ""

        if (card.hasImage()) {

            val url = "${sharedPreferences.getString("api_address", "http://192.168.1.8")}/${card.imagePath}"
            Toast.makeText(this, "Loading image from $url", Toast.LENGTH_SHORT).show()

            LoadImage(showCardImage, this).execute(url)
        }
        if (card.isMine) {
            showCardButtons.visibility = View.VISIBLE

            showCardEditButton.setOnClickListener {
                Intent(this, EditCardActivity::class.java).also { intent ->
                    intent.putExtra("card", card)
                    startActivityForResult(intent, REQUEST_EDIT_CARD)
                }
            }

            showCardDeleteButton.setOnClickListener {
                val dialog = DeleteCardDialog()
                dialog.setCallback {
                    deleteCard()
                }
                dialog.show(supportFragmentManager, "delete_card")
            }
        }
    }

    private fun deleteCard () {
        Toast.makeText(this, "Deleting card #${card.id}", Toast.LENGTH_SHORT).show()
        val url = "${sharedPreferences.getString("api_address", "http://192.168.1.8")}/business-cards/${card.id}"
        val request = StringRequest(Request.Method.DELETE, url, {
            Toast.makeText(this, "We deleted card #${card.id}!", Toast.LENGTH_SHORT).show()
            setResult(true)
            finish()
        }, {
            Toast.makeText(this, "Couldn't delete the card", Toast.LENGTH_SHORT).show()
            Log.e("delete error", it.message)
        })

        request.tag = "delete_card"
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this)
        }
        requestQueue?.add(request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EDIT_CARD) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val card = data.getSerializableExtra("new_card") as BusinessCard
                setFields(card)
                wasEdited = true
                setResult()
            }
        }
    }

    fun setResult (needsDelete: Boolean = false) {
        if (wasEdited || needsDelete) {
            val intent = Intent()
            if (wasEdited) {
                intent.putExtra("cardToEdit", card)
            } else {
                intent.putExtra("cardToDelete", card.id)
            }
            setResult(Activity.RESULT_OK, intent)
        }
    }
}


