package jp.original_app.ui.list

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import jp.original_app.R
import jp.original_app.Report
import jp.original_app.UsersPATH
import jp.original_app.reportPATH
import kotlinx.android.synthetic.main.report_edit_fragment.*
import java.util.HashMap

class ReportEditFragment : Fragment() {
    private val args: ReportEditFragmentArgs by navArgs()
    private lateinit var mReport: Report

    companion object {
        fun newInstance() = ReportEditFragment()
    }

    private lateinit var viewModel: ReportEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.report_edit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var temperaturelist = listOf("35.5℃","35.6℃","35.7℃","35.8℃","35.9℃","36.0℃"
            ,"36.1℃","36.2℃","36.3℃","36.4℃","36.5℃","36.6℃","36.7℃","36.8℃","36.9℃","37.0℃"
            ,"37.1℃","37.2℃","37.3℃","37.4℃","37.5℃","37.6℃","37.7℃","37.8℃","37.9℃","38.0℃"
            ,"38.1℃","38.2℃","38.3℃","38.4℃","38.5℃","38.6℃","38.7℃","38.8℃","38.9℃","39.0℃")
        var conditionlist = listOf("良い","普通","悪い")

        //アダプターを設定
        val temperatureAdapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_item,
            temperaturelist
        )
        val conditionAdapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_item,
            conditionlist
        )

        temperature_spinner.adapter = temperatureAdapter
        temperature_spinner.setSelection(10)
        condition_spinner.adapter = conditionAdapter
        condition_spinner.setSelection(1)

        mReport = args.report

        day.text = mReport.date
        remark_editText.setText(mReport.remark, TextView.BufferType.NORMAL)

        back_button.setOnClickListener {
            fragmentManager!!.popBackStack()
        }
        send_button.setOnClickListener{
            val temperature = temperature_spinner.selectedItem.toString()
            val condition = condition_spinner.selectedItem.toString()
            val remark = remark_editText.text.toString()
            val user = FirebaseAuth.getInstance().currentUser
            val databaseReference = FirebaseDatabase.getInstance().reference
            val reportReference = databaseReference.child(UsersPATH).child(user!!.uid).child(reportPATH).child(mReport.date)
            val data = HashMap<String, String>()

            data["temperature"] = temperature
            data["condition"] = condition
            data["remark"] = remark
            data["orderCnt"] = mReport.cnt

            reportReference.setValue(data)
            Toast.makeText(context, "登録が完了しました", Toast.LENGTH_SHORT).show()
            fragmentManager!!.popBackStack()
        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReportEditViewModel::class.java)
    }
}
