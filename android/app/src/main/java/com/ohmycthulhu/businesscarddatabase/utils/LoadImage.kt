package com.ohmycthulhu.businesscarddatabase.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.widget.ImageView
import android.widget.Toast
import java.io.InputStream
import java.net.URL


class LoadImage() : AsyncTask<String, Void, Drawable>() {
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
            // Toast.makeText(mContext, "It returned null", Toast.LENGTH_SHORT).show()
        }
    }
}