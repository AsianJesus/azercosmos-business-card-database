package com.ohmycthulhu.businesscarddatabase

import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_show_card.*
import java.io.InputStream
import java.net.URL

class ShowCardActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_card)

        sharedPreferences = getSharedPreferences("com.ohmycthulhu.businesscarddatabase", Context.MODE_PRIVATE)

        val card = intent.getSerializableExtra("card") as BusinessCard?

        if (card == null) {
            Toast.makeText(this, "Card is null", Toast.LENGTH_SHORT).show()
        } else {
            showCardName.text = card.name
            showCardCompany.text = card.company ?: ""
            showCardPosition.text = card.position ?: ""
            showCardAddress.text = card.address ?: ""
            showCardPosition.text = card.website ?: ""
            showCardPhone.text = card.phone ?: ""

            if (card.imagePath != null) {

                val url = "${sharedPreferences.getString("api_address", "http://192.168.1.8")}/images/1556898701.jpeg"
                Toast.makeText(this, "Loading image from $url", Toast.LENGTH_SHORT).show()

                LoadImage(showCardImage, this).execute(url)
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
