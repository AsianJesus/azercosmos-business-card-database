package com.ohmycthulhu.businesscarddatabase.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.ohmycthulhu.businesscarddatabase.R
import com.ohmycthulhu.businesscarddatabase.callbacks.AfterCardDeleteCallback
import com.ohmycthulhu.businesscarddatabase.callbacks.AfterCardEditCallback
import com.ohmycthulhu.businesscarddatabase.callbacks.BusinessCardController
import com.ohmycthulhu.businesscarddatabase.data.BusinessCard


class BusinessCardsAdapter(private val dataSet: ArrayList<BusinessCard>, private var context: Activity, private val controller: BusinessCardController) :
    BaseExpandableListAdapter(), AfterCardDeleteCallback,
    AfterCardEditCallback {

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convView = convertView
        val inflater = context.layoutInflater
        val card = getChild(groupPosition, childPosition) as BusinessCard
        if (convView == null) {
            convView = inflater.inflate(R.layout.list_child_item, null)
        }
        if (convView != null) {
            convView.findViewById<TextView>(R.id.listItemID).text = card.id
            convView.findViewById<TextView>(R.id.listItemAddress).text = card.address
            convView.findViewById<TextView>(R.id.listItemEmail).text = card.email
            convView.findViewById<TextView>(R.id.listItemNote).text = card.note
            convView.findViewById<TextView>(R.id.listItemPhone).text = card.phone
            convView.findViewById<TextView>(R.id.listItemWebsite).text = card.website
            convView.findViewById<TextView>(R.id.listItemPrivacy).text = if(card.private) "Private" else "Public"
            if (!card.isMine) {
                convView.findViewById<ImageView>(R.id.listItemDelete).visibility = View.GONE
                convView.findViewById<ImageView>(R.id.listItemEdit).visibility = View.GONE
            } else {
                convView.findViewById<ImageView>(R.id.listItemDelete).setOnClickListener {
                    controller.deleteCard(card.id, this)
                }
                convView.findViewById<ImageView>(R.id.listItemEdit).setOnClickListener {
                    controller.editCard(card, this)
                }
            }
            if (!card.hasImage()) {
                convView.findViewById<ImageView>(R.id.listItemImage).visibility = View.GONE
            } else {
                convView.findViewById<ImageView>(R.id.listItemImage).setOnClickListener {
                    controller.showImage(card)
                }
            }
            if (!(card.hasImage() || card.isMine)) {
                convView.findViewById<LinearLayout>(R.id.listItemToolbar).visibility = View.GONE
            }
            convView.setBackgroundColor(Color.parseColor("#ffe3e3e3"))
        }
        return convView as View
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var conv: View? = convertView
        val card = getGroup(groupPosition) as BusinessCard
        if (conv == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            conv = layoutInflater.inflate(R.layout.list_item, null)
        }
        if (conv != null) {
            val nameField: TextView = conv.findViewById(R.id.listItemName)
            val companyField = conv.findViewById<TextView>(R.id.listItemCompany)
            nameField.typeface = Typeface.DEFAULT_BOLD
            nameField.text = card.name
            companyField.text = "${card.company} - ${card.position}"
            conv.setBackgroundColor(Color.parseColor(if (isExpanded) "#ffe3e3e3" else "#00ffffff"))
            // conv.findViewById<RelativeLayout>(R.id.listItemLayout).setBackgroundColor(Color.MAGENTA)
        }
        return conv as View
    }

    override fun afterDelete(id: String) {
        /*
            For deleting we are checking dataSet from the end. It allows us to not use invalid indexes after deleting card
            After reversing array, all existing indexes are mapped as i => size - i - 1,
            so we need to get size and map them back for deleting
         */
        val length = dataSet.size
        for ((index,  card) in dataSet.reversed().withIndex()) {
            if (card.id == id) {
                dataSet.removeAt(length - 1 - index)
            }
        }
        notifyDataSetChanged()

    }

    override fun afterEdit(card: BusinessCard) {
        /*
            Search for card with needed id and replace if find
         */
        for ((index,  c) in dataSet.withIndex()) {
            if (c.id == card.id) {
                dataSet[index] = card
            }
        }
        notifyDataSetChanged()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getGroup(groupPosition: Int): Any {
        return dataSet[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return dataSet[groupPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return dataSet.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return dataSet[groupPosition].getNumericID()
        // return groupPosition.toLong()
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}/*
class BusinessCardsAdapter(private val dataSet: ArrayList<BusinessCard>, private var activity: Activity) :
    ArrayAdapter<BusinessCard>(activity, R.layout.list_item, dataSet), View.OnClickListener {

    var lastPosition: Int = -1

    private class ViewHolder {
        internal var txtName: TextView? = null
        internal var txtCompany: TextView? = null

    }

    override fun onClick(v: View?) {
        val position = v?.tag as Int
        val obj = getItem(position) as BusinessCard

        when(v.id) {
            R.id.listItemName -> Snackbar.make(v, "Business card belongs to ${obj.name}", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        *//*val view: View = View.inflate(activity, R.layout.list_item, null)
        val cardName = view.findViewById(R.id.listItemName) as TextView
        val cardCompany = view.findViewById(R.id.listItemCompany) as TextView
        Log.d("adapter_name", dataSet[position].id.toString())
        cardName.text = dataSet[position].name
        cardCompany.text = "Text"
        // cardCompany.text = "${dataSet[position].company} - ${dataSet[position].position}"

        return view*//*

        val card = getItem(position)



        var convView = convertView
        val viewHolder: ViewHolder?
        val result: View

        if (convView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convView = inflater.inflate(R.layout.list_item, parent, false)
            viewHolder.txtName = convView.findViewById(R.id.listItemName)
            viewHolder.txtCompany = convView.findViewById(R.id.listItemCompany)
            result = convView
        } else {
            viewHolder = convView.tag as ViewHolder?
            result = convView
        }
        val animation = AnimationUtils.loadAnimation(activity,
            if(position > lastPosition) R.anim.top_from_bottom else R.anim.down_from_top)

        result.startAnimation(animation)
        lastPosition = position

        if (viewHolder != null) {
            viewHolder.txtName!!.text = card!!.name
            viewHolder.txtCompany!!.text = "${card.company} - ${card.position}"
            viewHolder.txtName!!.setOnClickListener(this)
            viewHolder.txtName!!.tag = position
        }

        return convView as View
    }

    fun deleteCard (id: Int) {
        *//*val cardsToDelete = arrayListOf<Int>()
        for ((index, c) in dataSet.withIndex()) {
            if (c.id == id) {
                Log.d("cardToDelete", "${c.id} - ${c.name}")
                remove(c)
                cardsToDelete.add(index)
            }
        }*//*
*//*
        for (index in cardsToDelete.reversed()) {
            dataSet.removeAt(index)
        }*//*
    }

    fun editCard (card: BusinessCard) {

    }
}*/
/*


class CustomAdapter(private val dataSet: ArrayList<DataModel>, internal var mContext: Context) :
    ArrayAdapter<DataModel>(mContext, R.layout.row_item, dataSet), View.OnClickListener {

    private var lastPosition = -1

    // View lookup cache
    private class ViewHolder {
        internal var txtName: TextView? = null
        internal var txtType: TextView? = null
        internal var txtVersion: TextView? = null
        internal var info: ImageView? = null
    }

    override fun onClick(v: View) {

        val position = v.tag as Int
        val `object` = getItem(position)
        val dataModel = `object` as DataModel

        when (v.id) {
            R.id.item_info -> Snackbar.make(v, "Release date " + dataModel!!.getFeature(), Snackbar.LENGTH_LONG)
                .setAction("No action", null).show()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val dataModel = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        val viewHolder: ViewHolder // view lookup cache stored in tag

        val result: View

        if (convertView == null) {

            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.row_item, parent, false)
            viewHolder.txtName = convertView!!.findViewById<View>(R.id.name) as TextView
            viewHolder.txtType = convertView.findViewById<View>(R.id.type) as TextView
            viewHolder.txtVersion = convertView.findViewById<View>(R.id.version_number) as TextView
            viewHolder.info = convertView.findViewById<View>(R.id.item_info) as ImageView

            result = convertView

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            result = convertView
        }

        val animation = AnimationUtils.loadAnimation(
            mContext,
            if (position > lastPosition) R.anim.up_from_bottom else R.anim.down_from_top
        )
        result.startAnimation(animation)
        lastPosition = position

        viewHolder.txtName!!.setText(dataModel!!.getName())
        viewHolder.txtType!!.setText(dataModel!!.getType())
        viewHolder.txtVersion!!.setText(dataModel!!.getVersion_number())
        viewHolder.info!!.setOnClickListener(this)
        viewHolder.info!!.setTag(position)
        // Return the completed view to render on screen
        return convertView
    }
}

*/
