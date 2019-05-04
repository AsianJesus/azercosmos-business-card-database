package com.ohmycthulhu.businesscarddatabase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.request.SimpleMultiPartRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_edit_card.*
import kotlinx.android.synthetic.main.activity_new_card_photo.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File

class EditCardActivity : AppCompatActivity() {

    private var image: Bitmap? = null
    lateinit var card: BusinessCard
    lateinit var sharedPreferences: SharedPreferences
    lateinit var requestQueue: RequestQueue
    private var isSaving: Boolean = false
    private var fileToDelete: File? = null

    private val REQUEST_IMAGE_CAPTURE = 2
    private val REQUEST_PICK_IMAGE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_card)

        sharedPreferences = getSharedPreferences("com.ohmycthulhu.businesscarddatabase", Context.MODE_PRIVATE)
        requestQueue = Volley.newRequestQueue(this)

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
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }

    }

    private fun saveChanges (): Boolean {

        if (isSaving) {
            Toast.makeText(this, "Already saving card", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Creating card", Toast.LENGTH_SHORT).show()
        }

        val name = editCardName.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        val company = editCardCompany.text.toString()

        val email = editCardEmail.text.toString()

        val phone = editCardPhone.text.toString()

        val position = editCardPosition.text.toString()

        val website = editCardWebsite.text.toString()

        val address = editCardAddress.text.toString()
        val note = editCardNote.text.toString()

        sendEditRequest(name, company, email, address, phone, website, position, editCardIsPrivate.isChecked, image, note)
        return true
    }

    private fun sendEditRequest(name: String, company: String, email: String, address: String, phone: String, website: String, position: String, private: Boolean, image: Bitmap?, note: String) {
        val request = SimpleMultiPartRequest(Request.Method.PUT,
            "${sharedPreferences.getString("api_address", "http://192.168.1.8")}/business-cards/${card.id}",
            Response.Listener {
                Toast.makeText(this, "It worked!", Toast.LENGTH_SHORT).show()
                if (fileToDelete != null) {
                    (fileToDelete as File).delete()
                }
                isSaving = false
                update(it)
                finish()
            }, Response.ErrorListener {
                Toast.makeText(this, "Error occurred: ${it.message}", Toast.LENGTH_LONG).show()
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
            Toast.makeText(this, "Path is $path", Toast.LENGTH_SHORT).show()
            try {
                fileToDelete = File(path)
            } catch (e: Exception) {
                Toast.makeText(this, "Error while opened file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            request.addFile("photo", getPath(Uri.parse(path)))
        }
        requestQueue.add(request)
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

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
            Toast.makeText(this, "You took photo!", Toast.LENGTH_SHORT).show()
            val bundle = data.extras
            if (bundle != null) {
                image = bundle.get("data") as Bitmap
            }
        }
        if (requestCode == REQUEST_PICK_IMAGE && data != null) {
            Toast.makeText(this, "You picked image", Toast.LENGTH_SHORT).show()
            val pickUri = data.data
            image = if(pickUri != null) MediaStore.Images.Media.getBitmap(contentResolver, pickUri) else null
        }
    }

    fun update(result: String) {
        val userID = sharedPreferences.getInt("user_id", 1)
        val obj = JSONObject(result)
        val note = if(obj.has("note")) obj.getString("note") else ""
        val imagePath = obj.getString("image_path")

        val card = BusinessCard(obj.getInt("id"), obj.getString("name"), obj.getString("company_name"),
            obj.getString("email"), obj.getString("address"), obj.getString("mobile"),
            obj.getString("website"), obj.getString("position"),
            obj.getInt("private") == 1, note, imagePath, obj.getInt("created_by") == userID)

        Intent().also {
            it.putExtra("new_card", card)
            setResult(Activity.RESULT_OK, it)
        }
    }
}