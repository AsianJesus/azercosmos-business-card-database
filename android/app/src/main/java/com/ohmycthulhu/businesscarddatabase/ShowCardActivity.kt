package com.ohmycthulhu.businesscarddatabase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_show_card.*
import java.io.InputStream
import java.net.URL

class ShowCardActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
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
            showCardEditButton.visibility = View.VISIBLE

            showCardEditButton.setOnClickListener {
                Intent(this, EditCardActivity::class.java).also { intent ->
                    intent.putExtra("card", card)
                    startActivityForResult(intent, REQUEST_EDIT_CARD)
                }
            }
        }
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
