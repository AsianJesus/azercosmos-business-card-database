package com.ohmycthulhu.businesscarddatabase.utils

import java.io.Serializable

class BusinessCard (id: Int, name: String, company: String?, email: String?, address: String?, phone: String?, website: String?, position: String?, private: Boolean, note: String?, imagePath: String?, isMine: Boolean) : Serializable {
    var id: Int
    var name: String
    var company: String?
    var position: String?
    var email: String?
    var address: String?
    var phone: String?
    var website: String?
    var private: Boolean
    var note: String?
    var imagePath: String?
    var isMine: Boolean

    init {
        this.id = id
        this.name = name
        this.company = company
        this.position = position
        this.email = email
        this.address = address
        this.phone = phone
        this.website = website
        this.private = private
        this.note = note
        this.imagePath = imagePath
        this.isMine = isMine
    }

    fun hasImage (): Boolean{
        return this.imagePath != null
                && (imagePath as String).isNotEmpty()
                && imagePath != "null"
    }

}