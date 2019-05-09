package com.ohmycthulhu.businesscarddatabase

interface BusinessCardController {
    fun deleteCard(id: Int, afterCardDeleteCallback: AfterCardDeleteCallback)
    fun editCard(card: BusinessCard, afterCardEditCallback: AfterCardEditCallback)
    fun showImage(card: BusinessCard)
}