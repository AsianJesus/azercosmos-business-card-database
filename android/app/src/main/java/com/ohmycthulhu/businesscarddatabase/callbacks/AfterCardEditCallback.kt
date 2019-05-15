package com.ohmycthulhu.businesscarddatabase.callbacks

import com.ohmycthulhu.businesscarddatabase.utils.BusinessCard

interface AfterCardEditCallback {
    fun afterEdit(card: BusinessCard)
}