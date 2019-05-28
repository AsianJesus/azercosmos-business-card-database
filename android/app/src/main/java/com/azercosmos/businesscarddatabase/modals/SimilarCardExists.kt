package com.azercosmos.businesscarddatabase.modals

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

class SimilarCardExists : DialogFragment() {
    private var callback: ((res: Boolean) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Similar card already exists!")
                .setPositiveButton("Create") { _, _ ->
                    callback?.invoke(true)
                }.setNegativeButton("Cancel") { _, _ ->
                    callback?.invoke(false)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setCallback(callback: (res: Boolean) -> Unit) {
        this.callback = callback
    }


}