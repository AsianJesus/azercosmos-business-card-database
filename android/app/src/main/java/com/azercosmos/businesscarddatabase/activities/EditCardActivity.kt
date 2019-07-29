package com.azercosmos.businesscarddatabase.activities

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.request.SimpleMultiPartRequest
import com.azercosmos.businesscarddatabase.R
import com.azercosmos.businesscarddatabase.data.BusinessCard
import com.azercosmos.businesscarddatabase.recognizer.Recognizer
import com.azercosmos.businesscarddatabase.utils.HelperClass
import com.azercosmos.businesscarddatabase.utils.LoadImage
import com.azercosmos.businesscarddatabase.recognizer.RecognizeTask
import com.azercosmos.businesscarddatabase.utils.RequestManager
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_new_card.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.ref.WeakReference

class EditCardActivity : CardActivityBase() {

    private var recognitionTask: RecognizeTask? = null
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
        setContentView(R.layout.activity_new_card)
        setupToolbar()

        sharedPreferences = getSharedPreferences("com.azercosmos.businesscarddatabase", Context.MODE_PRIVATE)

        setResult(Activity.RESULT_CANCELED)

        val intent = intent
        val card = intent.getSerializableExtra("card") as BusinessCard?

        if (card != null) {
            this.card = card
            cardName.setText(card.name)
            cardCompany.setText(card.company)
            cardPosition.setText(card.position)
            cardAddress.setText(card.address)
            cardPhone.setText(card.phone)
            cardWebsite.setText(card.website)
            cardEmail.setText(card.email)
            cardNote.setText(card.note)
            if (card.imagePath != null) {
                val url = "${RequestManager.getServerUrl()}/${card.imagePath}"
                LoadImage(
                    cardImage,
                    this
                ).execute(url)
            }
            cardIsPrivate.isChecked = card.private

            saveButton.setOnClickListener {
                saveChanges()
            }
        }

        choosePhotoFab.setOnClickListener {
            Intent().also {
                it.type = "image/*"
                it.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(it, "Select picture"), REQUEST_PICK_IMAGE)
            }
        }

        makePhotoFab.setOnClickListener {
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
            return false
        }

        val name = cardName.text.toString()
        val company = cardCompany.text.toString()
        val position = cardPosition.text.toString()
        val email = cardEmail.text.toString()
        val phone = cardPhone.text.toString()
        val website = cardWebsite.text.toString()
        val address = cardAddress.text.toString()
        val note = cardNote.text.toString()

        val error = HelperClass.validate(name, company, position, email, website)
        if (error != null) {
            Toast.makeText(this, "$error is invalid", Toast.LENGTH_SHORT).show()
            return false
        }

        saveButton.isEnabled = false
        sendEditRequest(name, company, email, address, phone, website, position, cardIsPrivate.isChecked, image, note)
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
                update(it, note)
                saveButton.isEnabled = true
                finish()
            }, Response.ErrorListener {
                // Toast.makeText(this, "Error occurred: ${it.message}", Toast.LENGTH_LONG).show()
                isSaving = false
                if (fileToDelete != null) {
                    (fileToDelete as File).delete()
                }
                RequestManager.handleError(it, this)
                saveButton.isEnabled = true
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
            request.addFile("photo", HelperClass.getPath(this, Uri.parse(path)))
        }
        RequestManager.sendRequest(request)
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
                Log.e("crop_error", UCrop.getError(data!!)?.message)
            } else {
                // Toast.makeText(this, "Crop is successful!", Toast.LENGTH_SHORT).show()
                if (data != null) {
                    val uri = UCrop.getOutput(data)
                    image = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    cardImage.setImageBitmap(image)
                    if (image != null) {
                        if (image != null && cardRecognition.isChecked) {
                            startRecognizing(image!!)
                        }
                    }
                }
            }
        }
    }

    fun update(result: String, note: String) {
        val userID = sharedPreferences.getInt("user_id", 1)
        val obj = JSONObject(result)
        // val note = if(obj.has("note")) obj.getString("note") else ""
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


    private fun startRecognizing(image: Bitmap) {
        recognitionTask =
            RecognizeTask(WeakReference(this), WeakReference(this))
        recognitionTask!!.execute(image)
    }
}