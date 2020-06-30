package jp.original_app

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class RoomAdapter(context: Context) : BaseAdapter() {
    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mRoomArrayList = ArrayList<String>()

    override fun getItem(position: Int): Any {
        return mRoomArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mRoomArrayList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_room, parent, false)
        }
        val roomText = convertView!!.findViewById<View>(R.id.roomListName) as TextView
        roomText.text = mRoomArrayList[position]

        return convertView
    }
    fun setReportArrayList(roomArrayList: ArrayList<String>) {
        mRoomArrayList = roomArrayList
    }
}