package com.azercosmos.businesscarddatabase.callbacks

import com.azercosmos.businesscarddatabase.data.BusinessCard

interface AfterCardEditCallback {
    fun afterEdit(card: BusinessCard)
}