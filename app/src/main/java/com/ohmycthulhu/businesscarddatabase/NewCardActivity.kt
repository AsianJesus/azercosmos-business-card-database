package com.ohmycthulhu.businesscarddatabase

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_card_photo.*

class NewCardActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_PICK_IMAGE = 2

    var image: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_card_photo)

        makePhotoFab.setOnClickListener { dispatchTakePhoto() }
        choosePhotoFab.setOnClickListener { dispatchChoosePhoto() }
        createButton.setOnClickListener { createCard() }
    }

    private fun dispatchTakePhoto () {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun dispatchChoosePhoto () {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_PICK_IMAGE)
    }

    private fun createCard (): Boolean {
        val name = newCardName.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val company = newCardCompany.text.toString()
        if (company.isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val email = newCardEmail.text.toString()
        if (email.isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val phone = newCardPhone.text.toString()
        if (phone.isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val position = newCardPosition.text.toString()
        if (position.isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val website = newCardWebsite.text.toString()
        if (website.isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        sendCreateRequest(name, company, email, phone, website, position, newCardIsPrivate.isChecked, image)
        return true
    }

    private fun sendCreateRequest (name: String, company: String, email: String, phone: String, website: String, position: String, private: Boolean, image: Bitmap?) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Toast.makeText(this, "You took photo!", Toast.LENGTH_SHORT).show()
        }
        if (requestCode == REQUEST_PICK_IMAGE) {
            Toast.makeText(this, "You picked image", Toast.LENGTH_SHORT).show()
        }
    }
}
