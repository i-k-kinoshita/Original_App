package jp.original_app

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class MemberAdapter(context: Context)  : BaseAdapter() {
    private var mLayoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mReportArrayList = ArrayList<Report>()

    override fun getItem(position: Int): Any {
        return mReportArrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mReportArrayList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_member, parent, false)
        }

        val nameText = convertView!!.findViewById<View>(R.id.memberListDate) as TextView
        nameText.text = mReportArrayList[position].name

        val temperatureText = convertView.findViewById<View>(R.id.memberListTemperature) as TextView
        temperatureText.text = mReportArrayList[position].temperature

        val conditionImage = convertView.findViewById<View>(R.id.memberListCondition) as ImageView
        if(mReportArrayList[position].condition == "良い"){
            conditionImage.setImageResource(R.drawable.good)
            convertView.setBackgroundColor(Color.rgb(240, 255, 240))
        }else if(mReportArrayList[position].condition == "普通"){
            conditionImage.setImageResource(R.drawable.usual)
            convertView.setBackgroundColor(Color.rgb(255, 255, 240))
        }else if(mReportArrayList[position].condition == "悪い"){
            conditionImage.setImageResource(R.drawable.bad)
            convertView.setBackgroundColor(Color.rgb(255, 240, 245))
        }
        val memoImage = convertView.findViewById<View>(R.id.memberListRemark) as ImageView
        if(mReportArrayList[position].remark != ""){
            memoImage.setImageResource(R.drawable.memo)
        }

        return convertView
    }
    fun setReportArrayList(reportArrayList: ArrayList<Report>) {
        mReportArrayList = reportArrayList
    }
}