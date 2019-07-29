package com.azercosmos.businesscarddatabase.recognizer

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Log
import com.android.volley.Request
import com.android.volley.request.SimpleMultiPartRequest
import com.android.volley.toolbox.RequestFuture
import com.azercosmos.businesscarddatabase.activities.CardActivityBase
import com.azercosmos.businesscarddatabase.utils.HelperClass
import com.azercosmos.businesscarddatabase.utils.RequestManager
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class RecognizeTask(
    private val context: WeakReference<Activity>,
    private val parent: WeakReference<CardActivityBase>
): AsyncTask<Bitmap, Void, Map<RecognizePatterns, String>>() {
    private var dialog: AlertDialog? = null
    private var mResult: Map<String, String>? = null
    private val isConnection: Boolean = if (context.get() != null) HelperClass.isThereConnection(
        context.get() as Context
    ) else false

    // Getters
    val result: Map<String, String>?
        get () = mResult
    val isRecognizing: Boolean
        get () = dialog != null

    override fun doInBackground(vararg params: Bitmap?): Map<RecognizePatterns, String> {
        try {
            val image = params[0]!!

            val results = if (isConnection) {
                val url = "${RequestManager.getServerUrl()}/business-cards/recognize"
                val future = RequestFuture.newFuture<String>()
                val request = SimpleMultiPartRequest(Request.Method.POST, url, future, future)
                val context = context.get()!!
                val bytes = ByteArrayOutputStream()
                context.runOnUiThread {
                    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path = MediaStore.Images.Media.insertImage(context.contentResolver, image, "Title", null)
                    request.addFile("card",
                        HelperClass.getPath(
                            context,
                            Uri.parse(path)
                        )
                    )
                    RequestManager.sendRequest(request)
                }
                // Toast.makeText(this, "Path is $path", Toast.LENGTH_SHORT).show()
                val s = future.get(30, TimeUnit.SECONDS)
                parseRecognitionResult(s)
            } else {
                Recognizer().recognize(image)
            }

            return results
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("recognition_error", "${e::class.java}, ${e.message}")
            return mapOf()
        }
    }

    override fun onPreExecute() {
        super.onPreExecute()
        val context = context.get()
        if (context != null) {
            dialog = AlertDialog.Builder(context)
                .setTitle("Recognizing")
                .setMessage("Recognizing..." + if (isConnection) "" else "\nConnection not found. Running offline.")
                .setNegativeButton("Terminate") { _, _ -> abortTask() }
                .show()
        }
    }

    override fun onPostExecute(result: Map<RecognizePatterns, String>?) {
        super.onPostExecute(result)
        if (result != null) {
            parent.get()?.fillFields(result)
        }
        dialog?.dismiss()
    }

    private fun abortTask () {
        cancel(true)
    }

    private val recognitionFields = mapOf(
        Pair(RecognizePatterns.NAME, "name"),
        Pair(RecognizePatterns.PHONE, "phone"),
        Pair(RecognizePatterns.PHONE, "mobile"),
        Pair(RecognizePatterns.ADDRESS, "address")
    )
    private fun parseRecognitionResult (data: String): Map<RecognizePatterns, String> {
        val result = mutableMapOf<RecognizePatterns, String>()
        try {
            val json = JSONObject(data)
            for (entry in recognitionFields) {
                if (!json.isNull(entry.value)) {
                    result[entry.key] = json.getString(entry.value)
                }
            }
            val emails = json.getJSONArray("email")
            if (emails.length() > 0) {
                result[RecognizePatterns.EMAIL] = emails[0] as String
            }
        } catch (ex: Exception) {}
        return result
    }
}