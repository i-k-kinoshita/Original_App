package jp.original_app

import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_report_detail.*

class ReportDetailActivity : AppCompatActivity() {

    private lateinit var mReport: Report
    private lateinit var mReportRef: DatabaseReference
    private var mUserName = ""

    private var postListener: ValueEventListener = object : ValueEventListener {
        @SuppressLint("RestrictedApi")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val map = dataSnapshot.value as Map<String, String>
            mUserName = map["name"] ?: ""

            if(mReport.name == mUserName){
                delete_button.visibility = VISIBLE
                edit_button.visibility = VISIBLE
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
        }
    }
    private var reportListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val map = dataSnapshot.value as Map<String, String>
            date.text = dataSnapshot.key ?: ""
            temperature.text = map["temperature"] ?: ""
            condition.text = map["condition"] ?: ""
            remark.text = map["remark"] ?: ""

        }
        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_detail)

        val user = FirebaseAuth.getInstance().currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference

        delete_button.setOnClickListener {
            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@ReportDetailActivity)

            builder.setTitle("削除")
            builder.setMessage(mReport.date + "を削除しますか")

            builder.setPositiveButton("OK"){_, _ ->
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
        edit_button.visibility = GONE
        delete_button.visibility = GONE

        val userRef = dataBaseReference.child(UsersPATH).child(user!!.uid)
        userRef.addListenerForSingleValueEvent(postListener)

    }

    override fun onResume() {
        super.onResume()

        val extras = intent.extras
        mReport = extras.get("report") as Report

        val user = FirebaseAuth.getInstance().currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mReportRef = dataBaseReference.child(UsersPATH).child(mReport.userUid).child(reportPATH).child(mReport.date)

        mReportRef.addListenerForSingleValueEvent(reportListener)


    }
}
