package jp.original_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.ArrayList

class ReportAdapter(context: Context) : BaseAdapter() {
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
            convertView = mLayoutInflater.inflate(R.layout.list_report, parent, false)
        }

        val dateText = convertView!!.findViewById<View>(R.id.textView1) as TextView
        dateText.text = mReportArrayList[position].date.toString()

        val temperatureText = convertView.findViewById<View>(R.id.textView2) as TextView
        temperatureText.text = mReportArrayList[position].temperature

        val conditionText = convertView.findViewById<View>(R.id.textView3) as TextView
        conditionText.text = mReportArrayList[position].condition

        return convertView
    }
    fun setReportArrayList(reportArrayList: ArrayList<Report>) {
        mReportArrayList = reportArrayList
    }

}