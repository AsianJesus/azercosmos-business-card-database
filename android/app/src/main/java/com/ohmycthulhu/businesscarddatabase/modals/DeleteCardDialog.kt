package com.ohmycthulhu.businesscarddatabase.modals

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment


class DeleteCardDialog : DialogFragment() {
    private var callback: ((result: Boolean) -> Unit)?= null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Delete card? You cannot undo this action!")
                .setPositiveButton("Delete anyway!") { dialog, which ->
                    callback?.invoke(true)
                }.setNegativeButton("Cancel") { dialog, which ->
                    callback?.invoke(false)
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setCallback (callback: (result: Boolean) -> Unit) {
        this.callback = callback
    }

}