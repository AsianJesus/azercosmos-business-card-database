package com.ohmycthulhu.businesscarddatabase

import android.app.Activity
import android.view.View
import android.widget.ArrayAdapter
import android.content.Context
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView



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
        /*val view: View = View.inflate(activity, R.layout.list_item, null)
        val cardName = view.findViewById(R.id.listItemName) as TextView
        val cardCompany = view.findViewById(R.id.listItemCompany) as TextView
        Log.d("adapter_name", dataSet[position].id.toString())
        cardName.text = dataSet[position].name
        cardCompany.text = "Text"
        // cardCompany.text = "${dataSet[position].company} - ${dataSet[position].position}"

        return view*/

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
        /*val cardsToDelete = arrayListOf<Int>()
        for ((index, c) in dataSet.withIndex()) {
            if (c.id == id) {
                Log.d("cardToDelete", "${c.id} - ${c.name}")
                remove(c)
                cardsToDelete.add(index)
            }
        }*/
/*
        for (index in cardsToDelete.reversed()) {
            dataSet.removeAt(index)
        }*/
    }

    fun editCard (card: BusinessCard) {

    }
}
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
