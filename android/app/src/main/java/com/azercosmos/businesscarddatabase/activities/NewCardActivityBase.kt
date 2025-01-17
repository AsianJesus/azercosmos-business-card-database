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
import com.android.volley.request.StringRequest
import com.azercosmos.businesscarddatabase.R
import com.azercosmos.businesscarddatabase.modals.SimilarCardExists
import com.azercosmos.businesscarddatabase.utils.HelperClass
import com.azercosmos.businesscarddatabase.recognizer.RecognizeTask
import com.azercosmos.businesscarddatabase.utils.RequestManager
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_new_card.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.ref.WeakReference

class NewCardActivityBase : CardActivityBase() {

    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_PICK_IMAGE = 2

    var image: Bitmap? = null
    var fileToDelete: File? = null
    var cameraImageUri: Uri? = null
    lateinit var sharedPreferences: SharedPreferences
    private var recognitionTask: RecognizeTask? = null
    private var isCreating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_card)
        setupToolbar()
        setResult(Activity.RESULT_CANCELED)

        sharedPreferences = getSharedPreferences("com.azercosmos.businesscarddatabase", Context.MODE_PRIVATE)

        makePhotoFab.setOnClickListener { dispatchTakePhoto() }
        choosePhotoFab.setOnClickListener { dispatchChoosePhoto() }
        saveButton.setOnClickListener { createCard() }
    }

    private fun dispatchTakePhoto () {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        cameraImageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        Log.d("uri_path", cameraImageUri?.path)
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun dispatchChoosePhoto () {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select picture"), REQUEST_PICK_IMAGE)
    }

    private fun createCard (): Boolean {
        if (isCreating) {
            Toast.makeText(this, "Card is already being created", Toast.LENGTH_SHORT).show()
            return false
        }
        // Toast.makeText(this, "Creating card", Toast.LENGTH_SHORT).show()
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

        isCreating = true
        saveButton.isEnabled = false
        RequestManager.sendRequest(checkSimilar(name, company, position) { exists ->
            saveButton.isEnabled = true
            if (exists) {
                val dialog = SimilarCardExists()
                dialog.setCallback {
                    if (it) {
                        sendCreateRequest(name, company, email, address, phone, website, position, cardIsPrivate.isChecked, image, note)
                    } else {
                        isCreating = false
                    }
                }
                dialog.show(supportFragmentManager, "similar_card")
            } else {
                sendCreateRequest(name, company, email, address, phone, website, position, cardIsPrivate.isChecked, image, note)
            }
        })
        return true
    }

    private fun sendCreateRequest (name: String, company: String, email: String, address: String, phone: String, website: String, position: String, private: Boolean, image: Bitmap?, note: String) {
        val request = SimpleMultiPartRequest(Request.Method.POST,
            "${RequestManager.getServerUrl()}/business-cards",
            Response.Listener {
            // Toast.makeText(this, "It worked!", Toast.LENGTH_SHORT).show()
            if (fileToDelete != null) {
                (fileToDelete as File).delete()
            }
            isCreating = false
            setResult(Activity.RESULT_OK)
            finish()
        }, Response.ErrorListener {
            // Toast.makeText(this, "Error occurred: ${it.message}", Toast.LENGTH_LONG).show()
            if (fileToDelete != null) {
                (fileToDelete as File).delete()
            }
            isCreating = false
            RequestManager.handleError(it, this)
        })
        request.addStringParam("name", name)
        request.addStringParam("company_name", company)
        request.addStringParam("position", position)
        request.addStringParam("email", email)
        request.addStringParam("mobile", phone)
        request.addStringParam("website", website)
        request.addStringParam("private", if (private) "1" else "0")
        request.addStringParam("address", address)
        request.addStringParam("note", note)
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
        request.tag = "new_card"
        // requestQueue.add(request)
        RequestManager.sendRequest(request)
    }

    fun checkSimilar (name: String, companyName: String, position: String, callback: (r: Boolean) -> Unit): StringRequest {
        val request = StringRequest(Request.Method.POST,
            "${RequestManager.getServerUrl()}/business-cards-one/exists" +
                    "?name=$name&company_name=$companyName&position=$position",
            Response.Listener {
                callback(it == "true")
            }, Response.ErrorListener {
                callback(false)
                RequestManager.handleError(it, this)
            })
        return request
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Toast.makeText(this, "You took photo!", Toast.LENGTH_SHORT).show()
            if (cameraImageUri != null) {
                val cropImageUri = Uri.fromFile(File(cacheDir, "sample"))
                UCrop.of(cameraImageUri as Uri, cropImageUri).withAspectRatio(17.0f, 10.0f).start(this)
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

    private fun startRecognizing(image: Bitmap) {
        recognitionTask =
            RecognizeTask(WeakReference(this), WeakReference(this))
        recognitionTask!!.execute(image)
    }
}
