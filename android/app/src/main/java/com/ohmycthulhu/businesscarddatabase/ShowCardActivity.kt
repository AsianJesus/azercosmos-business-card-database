package com.ohmycthulhu.businesscarddatabase

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.request.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_show_card.*
import java.io.InputStream
import java.net.URL

class ShowCardActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var card: BusinessCard
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

            val url = "${sharedPreferences.getString("api_address", "http://192.168.1.8")}/images/1556898701.jpeg"
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
                dialog.setActivity(this)
                dialog.show(supportFragmentManager, "delete_card")
            }
        }
    }

    fun deleteCard () {
        Toast.makeText(this, "Deleting card #${card.id}", Toast.LENGTH_SHORT).show()
        val url = "${sharedPreferences.getString("api_address", "http://192.168.1.8")}/business-cards/${card.id}"
        val request = StringRequest(Request.Method.DELETE, url, {
            Toast.makeText(this, "We deleted card #${card.id}!", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
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
            }
        }
    }
}

private class LoadImage() : AsyncTask<String, Void, Drawable>() {
    private lateinit var imageView: ImageView
    private lateinit var mContext: Context

    constructor(view: ImageView, context: Context) : this(){
        this.imageView = view
        this.mContext = context
    }

    override fun doInBackground(vararg params: String?): Drawable? {
        try {
            val url = URL(params[0])
            val drawable = Drawable.createFromStream(url.content as InputStream, "src")
            return drawable
        } catch (e: Exception) {
            return null
        }
    }

    override fun onPostExecute(result: Drawable?) {
        super.onPostExecute(result)
        if (result != null) {
            imageView.setImageDrawable(result)
        } else {
            Toast.makeText(mContext, "It returned null", Toast.LENGTH_SHORT).show()
        }
    }
}

class DeleteCardDialog : DialogFragment() {
    private var showCardActivity: ShowCardActivity? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Delete card? You cannot undo this action!")
                .setPositiveButton("Delete anyway!") { dialog, which ->
                    showCardActivity?.deleteCard()
                }.setNegativeButton("Cancel") { dialog, which ->

                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setActivity (showCardActivity: ShowCardActivity) {
        this.showCardActivity = showCardActivity
    }

}

