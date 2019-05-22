package com.ohmycthulhu.businesscarddatabase.callbacks

import com.ohmycthulhu.businesscarddatabase.data.BusinessCard

interface AfterCardEditCallback {
    fun afterEdit(card: BusinessCard)
}