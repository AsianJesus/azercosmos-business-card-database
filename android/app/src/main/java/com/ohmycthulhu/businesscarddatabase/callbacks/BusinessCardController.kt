package com.ohmycthulhu.businesscarddatabase.callbacks

import com.ohmycthulhu.businesscarddatabase.utils.BusinessCard

interface BusinessCardController {
    fun deleteCard(id: String, afterCardDeleteCallback: AfterCardDeleteCallback)
    fun editCard(card: BusinessCard, afterCardEditCallback: AfterCardEditCallback)
    fun showImage(card: BusinessCard)
}