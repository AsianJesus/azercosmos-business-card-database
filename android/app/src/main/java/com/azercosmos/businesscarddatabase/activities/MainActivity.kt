package com.azercosmos.businesscarddatabase.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.SearchView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.request.JsonArrayRequest
import com.android.volley.request.StringRequest
import com.azercosmos.businesscarddatabase.BuildConfig
import com.azercosmos.businesscarddatabase.R
import com.azercosmos.businesscarddatabase.callbacks.AfterCardDeleteCallback
import com.azercosmos.businesscarddatabase.callbacks.AfterCardEditCallback
import com.azercosmos.businesscarddatabase.callbacks.BusinessCardController
import com.azercosmos.businesscarddatabase.data.BusinessCard
import com.azercosmos.businesscarddatabase.modals.DeleteCardDialog
import com.azercosmos.businesscarddatabase.modals.ShowImageModal
import com.azercosmos.businesscarddatabase.utils.AssetsDecompressor
import com.azercosmos.businesscarddatabase.utils.BusinessCardsAdapter
import com.azercosmos.businesscarddatabase.utils.HelperClass
import com.azercosmos.businesscarddatabase.utils.RequestManager
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import kotlinx.android.synthetic.main.content_main.*
import org.json.JSONObject
import kotlin.math.min
import com.ferfalk.simplesearchview.SimpleSearchView
import kotlinx.android.synthetic.main.filter_layout.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : BaseActivity(), BusinessCardController {

    /*
        Codes for activity results
     */
    private val REQUEST_NEW_CARD = 3
    private val REQUEST_EDIT_CARD = 4

    private val timer: Timer = Timer()

    override val menuRes: Int = if (BuildConfig.DEBUG) R.menu.menu_main_debug else R.menu.menu_main


    lateinit var sharedPreferences: SharedPreferences

    /*
        These variables needed for editing cards
        afterEditCallback is called after EditCard activity ends up with Result OK
     */
    var afterCardEditCallback: AfterCardEditCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        setupToolbar()
        setResult(LoginActivity.ACTION.ACTION_FINISH.getCode())

        val assetsPath = "${Environment.getExternalStorageDirectory()}/${getString(R.string.assets_path)}"
        AssetsDecompressor.unpack(baseContext.assets, assetsPath, "tessdata")

        sharedPreferences = getSharedPreferences("com.azercosmos.businesscarddatabase", Context.MODE_PRIVATE)

        fab.setOnClickListener { view ->
            Intent(this, NewCardActivity::class.java).also {
                startActivityForResult(it, REQUEST_NEW_CARD)
            }
        }

        val context = this
        searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query != "") {
                    context.loadCards(query)
                } else {
                    context.loadCards(filters = loadFilters())
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextCleared(): Boolean {
                return false
            }
        })

        filterOverlay.setOnClickListener {
            filterState = false
        }

        fbApply.setOnClickListener {
            saveFilters()
            loadCards(filters = loadFilters())
            filterState = false
        }

        fbClear.setOnClickListener {
            clearFilters()
            loadCards(filters = loadFilters())
        }

        val filters = loadFilters()
        applyFilters(filters)

        loadCards(filters = filters)
    }

    private fun loadCards (searchQuery: String = "", filters: MutableMap<String, String> = mutableMapOf()) {
        val request = JsonArrayRequest(Request.Method.GET,
            "${RequestManager.getServerUrl()}/business-cards?search=$searchQuery&${HelperClass.filtersToQuery(filters)}",
            null,
            Response.Listener {
                val userID = sharedPreferences.getInt("user_id", 1)
                val cards: ArrayList<BusinessCard> = ArrayList(it.length())
                for (i in 0 until it.length()) {
                    val obj = it[i] as JSONObject
                    val notes = obj.getJSONArray("notes")
                    val note = if (notes.length() > 0) (notes[0] as JSONObject).getString("note") else ""
                    val imagePath = obj.getString("image_path")

                    cards.add(
                        BusinessCard(
                            obj.getString("id"), obj.getString("name"), obj.getString("company_name"),
                            obj.getString("email"), obj.getString("address"), obj.getString("mobile"),
                            obj.getString("website"), obj.getString("position"),
                            obj.getInt("private") == 1, note, imagePath, obj.getInt("created_by") == userID
                        )
                    )
                }
                showCards(cards)
            },
            Response.ErrorListener {
                if (it.networkResponse.statusCode == 500) {
                    logout()
                }
                RequestManager.handleError(it, this)
                // Toast.makeText(this, "Error occurred on loading cards: ${it.message}", Toast.LENGTH_LONG).show()
            })
        request.tag = "load_cards"
        request.setShouldCache(false)
        RequestManager.sendRequest(request)
    }

    override fun deleteCard(id: String, afterCardDeleteCallback: AfterCardDeleteCallback) {
        val dialog = DeleteCardDialog()
        dialog.setCallback {
            if (it) {
                // Toast.makeText(this, "Card $id is deleted", Toast.LENGTH_SHORT).show()
                sendDeleteRequest(id) {
                    afterCardDeleteCallback.afterDelete(id)
                }
            }
        }
        dialog.show(supportFragmentManager, "delete_card")
    }

    override fun editCard(card: BusinessCard, afterCardEditCallback: AfterCardEditCallback) {
        // Toast.makeText(this, "Card ${card.id} is edited", Toast.LENGTH_SHORT).show()
        this.afterCardEditCallback = afterCardEditCallback
        Intent(this, EditCardActivity::class.java).also {
            it.putExtra("card", card)
            startActivityForResult(it, REQUEST_EDIT_CARD)
        }
    }

    override fun showImage(card: BusinessCard) {
        /*
            Open modal window with current image
         */
        val url = "${RequestManager.getServerUrl()}/${card.imagePath}"
        // Toast.makeText(this, "Image is at $url", Toast.LENGTH_SHORT).show()
        val modal = ShowImageModal()
        modal.setImageURL(url)
        modal.dialog// .window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        modal.show(supportFragmentManager, "show_image")
    }

    private fun sendDeleteRequest(id: String, callback: () -> Unit) {
        val url = "${RequestManager.getServerUrl()}/business-cards/$id"
        val request = StringRequest(Request.Method.DELETE, url, {
            // Toast.makeText(this, "We deleted card #$id!", Toast.LENGTH_SHORT).show()
            callback()
        }, {
            RequestManager.handleError(it, this)
            // Toast.makeText(this, "Couldn't delete the card", Toast.LENGTH_SHORT).show()
            Log.e("delete error", it.message)
        })

        request.tag = "delete_card"
        RequestManager.sendRequest(request)
        // requestQueue.add(request)
    }

    private fun showCards (cards: ArrayList<BusinessCard>) {
        val adapter = BusinessCardsAdapter(cards, this, this, cardsList)
        cardsList.setAdapter(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.action_filter -> {
                filterState = !filterState
            }
            R.id.action_search -> {
                searchView.showSearch()
            }
            R.id.action_refresh -> {
                loadCards(filters = loadFilters())
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CONFIG) {
            if (resultCode == Activity.RESULT_OK) {
                loadCards(filters = loadFilters())
            }
        }
        if (requestCode == REQUEST_NEW_CARD) {
            if (resultCode == Activity.RESULT_OK) {
                loadCards(filters = loadFilters())
            }
        }
        if (requestCode == REQUEST_EDIT_CARD) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val card = data.getSerializableExtra("new_card") as BusinessCard
                if (afterCardEditCallback == null) {
                    Log.d("aftercard", "Callback is null")
                } else {
                    Log.d("aftercard", "Callback is not null")
                }
                afterCardEditCallback?.afterEdit(card)
            }
        }
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
            return
        } else {
            super.onBackPressed()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        cardsList.setIndicatorBounds(cardsList.right - min(cardsList.width / 10, 100)
            , cardsList.width)
    }

    private fun applyFilters(filters: MutableMap<String, String>) {
        ftName.setText(filters["name"])
        ftCompany.setText(filters["company_name"])
        ftPosition.setText(filters["position"])
        ftAddress.setText(filters["address"])
        ftEmail.setText(filters["email"])
        ftPhone.setText(filters["mobile"])
        ftWebsite.setText(filters["website"])
    }
    private fun saveFilters() {
        sharedPreferences.edit()
            .putString("filter_main_name", ftName.text.toString())
            .putString("filter_main_company", ftCompany.text.toString())
            .putString("filter_main_position", ftPosition.text.toString())
            .putString("filter_main_address", ftAddress.text.toString())
            .putString("filter_main_phone", ftPhone.text.toString())
            .putString("filter_main_email", ftEmail.text.toString())
            .putString("filter_main_website", ftWebsite.text.toString())
            .apply()
    }
    private fun loadFilters(): MutableMap<String, String> {
        val result = mutableMapOf<String, String>()
        sharedPreferences.getString("filter_main_name", "").let {
            if (it != null && it.isNotEmpty()) {
                result["name"] = it
            }
        }
        sharedPreferences.getString("filter_main_company", "").let {
            if (it != null && it.isNotEmpty()) {
                result["company_name"] = it
            }
        }
        sharedPreferences.getString("filter_main_position", "").let {
            if (it != null && it.isNotEmpty()) {
                result["position"] = it
            }
        }
        sharedPreferences.getString("filter_main_address", "").let {
            if (it != null && it.isNotEmpty()) {
                result["address"] = it
            }
        }
        sharedPreferences.getString("filter_main_phone", "").let {
            if (it != null && it.isNotEmpty()) {
                result["mobile"] = it
            }
        }
        sharedPreferences.getString("filter_main_email", "").let {
            if (it != null && it.isNotEmpty()) {
                result["email"] = it
            }
        }
        sharedPreferences.getString("filter_main_website", "").let {
            if (it != null && it.isNotEmpty()) {
                result["website"] = it
            }
        }
        return result
    }

    private fun clearFilters() {
        applyFilters(mutableMapOf())
        saveFilters()
    }

    private var filterState: Boolean
            get() = filterHolder.visibility == View.VISIBLE
            set (value) {
                val animation = AnimationUtils.loadAnimation(this, if (value) R.anim.slide_left else R.anim.slide_right)
                filterBody.startAnimation(animation)
                if (value) {
                    filterHolder.visibility = View.VISIBLE
                    filterOverlay.visibility = View.GONE
                } else{
                    filterHolder.visibility = View.VISIBLE
                    filterOverlay.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.abc_fade_out))
                }
                timer.schedule(object: TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            if (!value) {
                                filterHolder.visibility = View.GONE
                            }
                            filterOverlay.visibility = filterHolder.visibility
                            if (value) {
                                filterOverlay.startAnimation(
                                    AnimationUtils.loadAnimation(applicationContext, R.anim.abc_fade_in)
                                )
                            }
                        }
                    }
                }, animation.duration)
                fab.visibility = if (value) View.GONE else View.VISIBLE
            }
}
