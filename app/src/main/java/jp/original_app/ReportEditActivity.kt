package jp.original_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_report_edit.*
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import java.text.SimpleDateFormat

class ReportEditActivity : AppCompatActivity() {

    private lateinit var mReport: Report


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_condition_send)

        var temperaturelist = listOf("35.5℃以下","35.6℃","35.7℃","35.8℃","35.9℃","36.0℃"
            ,"36.1℃","36.2℃","36.3℃","36.4℃","36.5℃","36.6℃","36.7℃","36.8℃","36.9℃","37.0℃"
            ,"37.1℃","37.2℃","37.3℃","37.4℃","37.5℃","37.6℃","37.7℃","37.8℃","37.9℃","38.0℃以上")
        var conditionlist = listOf("良い","普通","悪い")

        //アダプターを設定
        val temperatureadapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            temperaturelist
        )
        val conditionadapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            conditionlist
        )
        temperature_spinner.adapter = temperatureadapter
        temperature_spinner.setSelection(10)
        condition_spinner.adapter = conditionadapter
        condition_spinner.setSelection(1)

        val extras = intent.extras
        mReport = extras.get("report") as Report

        day.text = mReport.date
        remark_editText.setText(mReport.remark, TextView.BufferType.NORMAL)


        back_button.setOnClickListener {
            finish()
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

            val view = findViewById<View>(android.R.id.content)
            Snackbar.make(view, "登録が完了しました。", Snackbar.LENGTH_LONG).show()

//            val intent = Intent(applicationContext, MainActivity::class.java)
//            startActivity(intent)
            finish()
        }
    }
}