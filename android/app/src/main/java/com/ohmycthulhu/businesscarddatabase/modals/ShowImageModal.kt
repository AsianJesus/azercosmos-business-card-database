package com.ohmycthulhu.businesscarddatabase.modals

import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.DialogFragment
import android.widget.TextView
import com.ohmycthulhu.businesscarddatabase.R
import com.ohmycthulhu.businesscarddatabase.utils.LoadImage
import java.io.File

class ShowImageModal : DialogFragment() {
    private var url: String = ""
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.show_image_modal, null)
            LoadImage(
                view.findViewById(R.id.showImageModalImage),
                context as Context
            ).execute(url)

            view.findViewById<TextView>(R.id.showImageModalSave).setOnClickListener {
                if (context != null) {
                    // Toast.makeText(context, "It was clicked", Toast.LENGTH_SHORT).show()
                    val directoryPath = "${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_DCIM}/BCD"
                    if (!File(directoryPath).exists()) {
                        File(directoryPath).mkdirs()
                    }
                    val file = File(directoryPath, "business-card")
                    val request = DownloadManager.Request(Uri.parse(url))
                        .setTitle("Downloading card")
                        .setDescription("Downloading")
                        .setDestinationUri(Uri.fromFile(file))
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                    val downloadManager = (context as Context).getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val downloadID = downloadManager.enqueue(request)
                    // Toast.makeText(context, "It was ended. Id is $downloadID", Toast.LENGTH_SHORT).show()
                }
            }
            // (dialog.window as Window).setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            builder.setView(view)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setImageURL (url: String): ShowImageModal {
        this.url = url
        return this
    }
}