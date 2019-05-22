package com.ohmycthulhu.businesscarddatabase.callbacks

import com.ohmycthulhu.businesscarddatabase.data.BusinessCard

interface BusinessCardController {
    fun deleteCard(id: String, afterCardDeleteCallback: AfterCardDeleteCallback)
    fun editCard(card: BusinessCard, afterCardEditCallback: AfterCardEditCallback)
    fun showImage(card: BusinessCard)
}