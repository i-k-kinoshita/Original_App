package jp.original_app

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_report_detail.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MemberListActivity : AppCompatActivity() {

    private lateinit var mAdapter: MemberAdapter
    private lateinit var mListView: ListView
    private lateinit var mMemberArrayList: ArrayList<String>
    private lateinit var mReportArrayList: ArrayList<Report>
    private lateinit var mReportRef: DatabaseReference
    private var mRoomName = ""
    private var mManagerUid = ""

    private val mEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val map = snapshot.value as Map<String, String>
            val userName = map["name"] ?: ""

            // 現在時刻の取得
            val cal = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy年M月dd日(E)")
            val date = sdf.format(cal.time)

            val key = snapshot.key ?: ""
            val databaseReference = FirebaseDatabase.getInstance().reference
            val reportRef = databaseReference.child(UsersPATH).child(key).child(reportPATH).child(date)

            reportRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value != null) {
                        // データあり
                        val temp = snapshot.value as Map<String, String>

                        val temperature = temp["temperature"] ?: ""
                        val condition = temp["condition"] ?: ""
                        val remark = temp["remark"] ?: ""
                        val orderCnt = temp["orderCnt"] ?: ""

                        val report = Report(date,temperature,condition,remark,orderCnt,userName,key)
                        mReportArrayList.add(report)
                        mAdapter.notifyDataSetChanged()
                    }else{
                        // データなし


                    }
                }
                override fun onCancelled(firebaseError: DatabaseError) {}
            })
        }
        override fun onCancelled(firebaseError: DatabaseError) {}
    }
    private var managerListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val map = dataSnapshot.value as Map<String, String>
            mManagerUid = map["manager"] ?: ""
        }
        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)

        val extras = intent.extras
        mMemberArrayList = extras.get("member") as ArrayList<String> // グループメンバーのUID
        mRoomName = extras.get("roomName") as String // グループの名前

        mReportArrayList = ArrayList<Report>()
        mListView = findViewById(R.id.listView)

        val user = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().reference
        val managerRef = databaseReference.child(roomPATH).child(mRoomName)
        val userRef = databaseReference.child(UsersPATH)
        managerRef.addListenerForSingleValueEvent(managerListener)



        mListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(applicationContext, ReportDetailActivity::class.java)
            intent.putExtra("report", mReportArrayList[position])
            startActivity(intent)
        }
        mListView.setOnItemLongClickListener { parent, view, position, id ->
            val report = parent.adapter.getItem(position) as Report

            Log.d("Kotlintest","reportUserUid:${report.userUid}")
            Log.d("Kotlintest","mManagerUid:$mManagerUid")
            Log.d("Kotlintest","UserUid:${user!!.uid}")


            if(user!!.uid == mManagerUid){
                if(report.userUid == user!!.uid){
                    val intent = Intent(applicationContext, ReportDetailActivity::class.java)
                    intent.putExtra("report", mReportArrayList[position])
                    startActivity(intent)

                }else{
                    // 他人なら
                    // ダイアログを表示する
                    val builder = AlertDialog.Builder(this@MemberListActivity)

                    builder.setTitle("削除")
                    builder.setMessage(report.name + "さんを退会させますか")

                    builder.setPositiveButton("OK"){_, _ ->
                        managerRef.child(UsersPATH).child(report.userUid).removeValue()
                        userRef.child(report.userUid).child(roomPATH).child(mRoomName).removeValue()

                        Snackbar.make(view, report.name + "さんを退会させました", Snackbar.LENGTH_LONG).show()
                        finish()
                    }

                    builder.setNegativeButton("CANCEL", null)

                    val dialog = builder.create()
                    dialog.show()

                }
            }else{
                // 管理者でなければ
                val intent = Intent(applicationContext, ReportDetailActivity::class.java)
                intent.putExtra("report", mReportArrayList[position])
                startActivity(intent)
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()

        val databaseReference = FirebaseDatabase.getInstance().reference
        mReportArrayList = ArrayList<Report>()

        for(i in 0 until mMemberArrayList.size){
            mReportRef = databaseReference.child(UsersPATH).child(mMemberArrayList[i])
            mReportRef.addListenerForSingleValueEvent(mEventListener)
        }

        mAdapter = MemberAdapter(this)
        mAdapter.setReportArrayList(mReportArrayList)
        mListView.adapter = mAdapter
    }
}
