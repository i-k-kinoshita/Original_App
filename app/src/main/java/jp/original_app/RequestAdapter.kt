package jp.original_app

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_notice.*
import java.util.ArrayList
import java.util.HashMap

class RequestAdapter (context: Context) :BaseAdapter() {
    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mRequestArrayList = ArrayList<Request>()

    override fun getItem(position: Int): Any {
        return mRequestArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mRequestArrayList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_request, parent, false)
        }

        val userName = convertView!!.findViewById<View>(R.id.requestUserName) as TextView
        userName.text = mRequestArrayList[position].userName

        val roomName = convertView.findViewById<View>(R.id.requestRoomName) as TextView
        roomName.text = mRequestArrayList[position].roomName

        val requestText = convertView.findViewById<View>(R.id.requestText) as TextView
        requestText.visibility = GONE

        val requestButton = convertView.findViewById<View>(R.id.requestButton) as Button
        val deleteButton = convertView.findViewById<View>(R.id.deleteButton) as Button

        val user = FirebaseAuth.getInstance().currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val requestRef = dataBaseReference.child(roomPATH).child(mRequestArrayList[position].roomName)
        val data = HashMap<String, String>()
        data["name"] = mRequestArrayList[position].userName

        requestButton.setOnClickListener {
            Log.d("Kotlintest","承認")
            requestButton.visibility = GONE
            deleteButton.visibility = GONE
            requestText.text = "承認しました"
            requestText.visibility = VISIBLE

            requestRef.child(requestPATH).child(mRequestArrayList[position].userUid).removeValue()
            requestRef.child(UsersPATH).child(mRequestArrayList[position].userUid).setValue(data)
        }

        deleteButton.setOnClickListener {
            Log.d("Kotlintest","削除")
            requestButton.visibility = GONE
            deleteButton.visibility = GONE
            requestText.text = "拒否しました"
            requestText.visibility = VISIBLE

            requestRef.child(requestPATH).child(mRequestArrayList[position].userUid).removeValue()
        }


        return convertView
    }
    fun setRequestArrayList(requestArrayList: ArrayList<Request>) {
        mRequestArrayList = requestArrayList
    }

}