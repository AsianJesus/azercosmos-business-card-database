package com.azercosmos.businesscarddatabase.callbacks

import com.azercosmos.businesscarddatabase.data.BusinessCard

interface BusinessCardController {
    fun deleteCard(id: String, afterCardDeleteCallback: AfterCardDeleteCallback)
    fun editCard(card: BusinessCard, afterCardEditCallback: AfterCardEditCallback)
    fun showImage(card: BusinessCard)
}