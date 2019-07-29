package com.azercosmos.businesscarddatabase.activities

import com.azercosmos.businesscarddatabase.recognizer.RecognizePatterns
import kotlinx.android.synthetic.main.activity_new_card.*

abstract class CardActivityBase : ActivityBase() {
    open fun fillFields (fields: Map<RecognizePatterns, String>) {
        if (fields.containsKey(RecognizePatterns.NAME)) {
            cardName.setText(fields[RecognizePatterns.NAME])
        }
        if (fields.containsKey(RecognizePatterns.PHONE)) {
            cardPhone.setText(fields[RecognizePatterns.PHONE])
        }
        if (fields.containsKey(RecognizePatterns.EMAIL)) {
            cardEmail.setText(fields[RecognizePatterns.EMAIL])
        }
        if (fields.containsKey(RecognizePatterns.ADDRESS)) {
            cardAddress.setText(fields[RecognizePatterns.ADDRESS])
        }
        if (fields.containsKey(RecognizePatterns.WEBSITE)) {
            cardAddress.setText(fields[RecognizePatterns.WEBSITE])
        }
    }
}