package com.ohmycthulhu.businesscarddatabase.activities

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.request.SimpleMultiPartRequest
import com.ohmycthulhu.businesscarddatabase.R
import com.ohmycthulhu.businesscarddatabase.data.BusinessCard
import com.ohmycthulhu.businesscarddatabase.recognizer.RecognizePatterns
import com.ohmycthulhu.businesscarddatabase.recognizer.Recognizer
import com.ohmycthulhu.businesscarddatabase.utils.RequestManager
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_edit_card.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File

class EditCardActivity : AppCompatActivity() {

    private var image: Bitmap? = null
    lateinit var card: BusinessCard
    lateinit var sharedPreferences: SharedPreferences
    private var isSaving: Boolean = false
    private var fileToDelete: File? = null
    private val recognizer: Recognizer = Recognizer()
    private var imageUri: Uri? = null // It's where camera picture is stored when photo is captured
    private val REQUEST_IMAGE_CAPTURE = 2
    private val REQUEST_PICK_IMAGE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)

        sharedPreferences = getSharedPreferences("com.ohmycthulhu.businesscarddatabase", Context.MODE_PRIVATE)

        setResult(Activity.RESULT_CANCELED)

        val intent = intent
        val card = intent.getSerializableExtra("card") as BusinessCard?

        if (card != null) {
            this.card = card
            editCardName.setText(card.name)
            editCardCompany.setText(card.company)
            editCardPosition.setText(card.position)
            editCardAddress.setText(card.address)
            editCardPhone.setText(card.phone)
            editCardWebsite.setText(card.website)
            editCardEmail.setText(card.email)
            editCardNote.setText(card.note)
            editCardIsPrivate.isChecked = card.private

            editCardSaveButton.setOnClickListener {
                saveChanges()
            }
        }

        editCardChoosePhotoFab.setOnClickListener {
            Intent().also {
                it.type = "image/*"
                it.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(it, "Select picture"), REQUEST_PICK_IMAGE)
            }
        }

        editCardMakePhotoFab.setOnClickListener {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }

    }

    private fun saveChanges (): Boolean {

        if (isSaving) {
            // Toast.makeText(this, "Already saving card", Toast.LENGTH_SHORT).show()
        } else {
            // Toast.makeText(this, "Editing card", Toast.LENGTH_SHORT).show()
        }

        val name = editCardName.text.toString()
        if (name.isEmpty()) {
            // Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val company = editCardCompany.text.toString()
        val position = editCardPosition.text.toString()
        if (company.isEmpty()) {
            Toast.makeText(this, "Company name is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        if (position.isEmpty()) {
            Toast.makeText(this, "Position is empty", Toast.LENGTH_SHORT).show()
            return false
        }
        val email = editCardEmail.text.toString()

        val phone = editCardPhone.text.toString()


        val website = editCardWebsite.text.toString()

        val address = editCardAddress.text.toString()
        val note = editCardNote.text.toString()

        sendEditRequest(name, company, email, address, phone, website, position, editCardIsPrivate.isChecked, image, note)
        return true
    }

    private fun sendEditRequest(name: String, company: String, email: String, address: String, phone: String, website: String, position: String, private: Boolean, image: Bitmap?, note: String) {
        val request = SimpleMultiPartRequest(Request.Method.PUT,
            "${RequestManager.getServerUrl()}/business-cards/${card.id}",
            Response.Listener {
                // Toast.makeText(this, "It worked!", Toast.LENGTH_SHORT).show()
                if (fileToDelete != null) {
                    (fileToDelete as File).delete()
                }
                isSaving = false
                update(it)
                finish()
            }, Response.ErrorListener {
                // Toast.makeText(this, "Error occurred: ${it.message}", Toast.LENGTH_LONG).show()
                isSaving = false
                if (fileToDelete != null) {
                    (fileToDelete as File).delete()
                }
            })
        isSaving = true
        request.addStringParam("name", name)
        request.addStringParam("company_name", company)
        request.addStringParam("position", position)
        request.addStringParam("email", email)
        request.addStringParam("mobile", phone)
        request.addStringParam("website", website)
        request.addStringParam("private", if (private) "1" else "0")
        request.addStringParam("address", address)
        request.addStringParam("note", note)

        request.tag = "edit_card"
        if (image != null) {
            val bytes = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(contentResolver, image, "Title", null)
            // Toast.makeText(this, "Path is $path", Toast.LENGTH_SHORT).show()
            try {
                fileToDelete = File(path)
            } catch (e: Exception) {
                // Toast.makeText(this, "Error while opened file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            request.addFile("photo", getPath(Uri.parse(path)))
        }
        RequestManager.sendRequest(request)
    }

    private fun getPath(uri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(applicationContext, uri, proj, null, null, null)
        val cursor = loader.loadInBackground()
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val result = cursor?.getString(columnIndex as Int)
        cursor?.close()
        return result as String
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Toast.makeText(this, "You took photo!", Toast.LENGTH_SHORT).show()
            if (imageUri != null) {
                val cropImageUri = Uri.fromFile(File(cacheDir, "sample"))
                UCrop.of(imageUri as Uri, cropImageUri).withAspectRatio(17.0f, 10.0f).start(this)
            }
        }
        if (requestCode == REQUEST_PICK_IMAGE && data != null) {
            // Toast.makeText(this, "You picked image", Toast.LENGTH_SHORT).show()
            val pickUri = data.data
            if (pickUri != null) {
                val cropImageUri = Uri.fromFile(File(cacheDir, "sample"))
                UCrop.of(pickUri as Uri, cropImageUri as Uri).withAspectRatio(17.0f, 10.0f).start(this)
            }
        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == UCrop.RESULT_ERROR) {
                // Toast.makeText(this, "Error on crop", Toast.LENGTH_SHORT).show()
                Log.e("crop_error", UCrop.getError(data as Intent)?.message)
            } else {
                // Toast.makeText(this, "Crop is successful!", Toast.LENGTH_SHORT).show()
                if (data != null) {
                    val uri = UCrop.getOutput(data as Intent)
                    image = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    if (image != null) {
                        if (image != null && editCardRecognition.isChecked) {
                            fillFields(recognizer.recognize(image as Bitmap))
                        }
                    }
                }
            }
        }
        /*if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Toast.makeText(this, "You took photo!", Toast.LENGTH_SHORT).show()
            image = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            if (image != null && editCardRecognition.isChecked) {
                image = ImageUtils.limitImageSize(image as Bitmap, 2000, 2000)
                fillFields(recognizer.recognize(image as Bitmap))
            }
        }
        if (requestCode == REQUEST_PICK_IMAGE && data != null) {
            // Toast.makeText(this, "You picked image", Toast.LENGTH_SHORT).show()
            val pickUri = data.data
            image = if(pickUri != null) MediaStore.Images.Media.getBitmap(contentResolver, pickUri) else null
            if (image != null && editCardRecognition.isChecked) {
                image = ImageUtils.limitImageSize(image as Bitmap, 2000, 2000)
                fillFields(recognizer.recognize(image as Bitmap))
            }
        }*/

    }
    private fun fillFields (fields: Map<RecognizePatterns, String>) {
        if (fields.containsKey(RecognizePatterns.NAME)) {
            editCardName.setText(fields[RecognizePatterns.NAME])
        }
        if (fields.containsKey(RecognizePatterns.PHONE)) {
            editCardPhone.setText(fields[RecognizePatterns.PHONE])
        }
        if (fields.containsKey(RecognizePatterns.EMAIL)) {
            editCardEmail.setText(fields[RecognizePatterns.EMAIL])
        }
    }

    fun update(result: String) {
        val userID = sharedPreferences.getInt("user_id", 1)
        val obj = JSONObject(result)
        val note = if(obj.has("note")) obj.getString("note") else ""
        val imagePath = obj.getString("image_path")

        val card = BusinessCard(
            obj.getString("id"), obj.getString("name"), obj.getString("company_name"),
            obj.getString("email"), obj.getString("address"), obj.getString("mobile"),
            obj.getString("website"), obj.getString("position"),
            obj.getInt("private") == 1, note, imagePath, obj.getInt("created_by") == userID
        )

        Intent().also {
            it.putExtra("new_card", card)
            setResult(Activity.RESULT_OK, it)
        }
    }
}