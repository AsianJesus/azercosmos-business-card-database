package com.ohmycthulhu.businesscarddatabase

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment


class DeleteCardDialog : DialogFragment() {
    private var deletesCard: DeletesCard? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Delete card? You cannot undo this action!")
                .setPositiveButton("Delete anyway!") { dialog, which ->
                    deletesCard?.deleteCard()
                }.setNegativeButton("Cancel") { dialog, which ->

                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setActivity (deletesCard: DeletesCard) {
        this.deletesCard = deletesCard
    }

}