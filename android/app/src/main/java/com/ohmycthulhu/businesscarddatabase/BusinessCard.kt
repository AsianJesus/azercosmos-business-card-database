package com.ohmycthulhu.businesscarddatabase

import java.io.Serializable

class BusinessCard (name: String, company: String?, email: String?, address: String?, phone: String?, website: String?, position: String?, private: Boolean, note: String?, imagePath: String?) : Serializable {
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

    init {
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
    }

}