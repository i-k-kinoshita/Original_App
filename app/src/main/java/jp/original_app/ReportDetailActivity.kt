package jp.original_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_report_detail.*

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var mReport: Report
    private lateinit var mReportRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)

        val extras = intent.extras
        mReport = extras.get("report") as Report

        delete_button.setOnClickListener {
            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@ReportDetailActivity)


            builder.setTitle("削除")
            builder.setMessage(mReport.date + "を削除しますか")

            builder.setPositiveButton("OK"){_, _ ->
                val user = FirebaseAuth.getInstance().currentUser
                val dataBaseReference = FirebaseDatabase.getInstance().reference
                mReportRef = dataBaseReference.child(UsersPATH).child(user!!.uid).child(reportPATH).child(mReport.date)
                mReportRef.removeValue()
                finish()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }

        edit_button.setOnClickListener {
            val intent = Intent(applicationContext, ReportEditActivity::class.java)
            intent.putExtra("report", mReport)
            startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()

        date.text = mReport.date
        temperature.text = mReport.temperature
        condition.text = mReport.condition
        remark.text = mReport.remark
    }
}
