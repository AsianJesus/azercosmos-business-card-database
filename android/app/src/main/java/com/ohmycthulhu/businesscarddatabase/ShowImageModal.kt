package com.ohmycthulhu.businesscarddatabase

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.ImageView
import java.lang.IllegalStateException

class ShowImageModal : DialogFragment() {
    private var url: String = ""
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.show_image_modal, null)
            LoadImage(view.findViewById(R.id.showImageModalImage), context as Context).execute(url)
            builder.setView(view)

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setImageURL (url: String): ShowImageModal {
        this.url = url
        return this
    }
}