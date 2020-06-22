package jp.original_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {

    private lateinit var mReportArrayList: ArrayList<Report>
    private lateinit var mAdapter: ReportAdapter
    private lateinit var mListView: ListView
    private lateinit var mReportRef: DatabaseReference

    private val mmmEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val map = snapshot.value as Map<String, String>
            val userName = map["name"] ?: ""

            val dataBaseReference = FirebaseDatabase.getInstance().reference
            val user = FirebaseAuth.getInstance().currentUser
            val userRef = dataBaseReference.child(UsersPATH).child(user!!.uid).child(reportPATH)

            userRef.limitToFirst(7).orderByChild("orderCnt").addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val date = p0.key ?: ""
                    val map = p0.value as Map<String, String>

                    val temperature = map["temperature"] ?: ""
                    val condition = map["condition"] ?: ""
                    val remark = map["remark"] ?: ""
                    val orderCnt = map["orderCnt"] ?: "-1"

                    val report = Report(date, temperature, condition, remark, orderCnt,userName,user!!.uid)
                    mReportArrayList.add(report)
                    mAdapter.notifyDataSetChanged()
                }
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                override fun onChildRemoved(p0: DataSnapshot) {}
                override fun onCancelled(firebaseError: DatabaseError) {}
            })
        }
        override fun onCancelled(firebaseError: DatabaseError) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        room.setOnClickListener { view ->
            val intent = Intent(applicationContext, RoomListActivity::class.java)
            startActivity(intent)
        }
        create.setOnClickListener { view ->
            val intent = Intent(applicationContext, ConditionSendActivity::class.java)
            startActivity(intent)
        }
        graph.setOnClickListener { view ->
            val intent = Intent(applicationContext, GraphActivity::class.java)
            intent.putExtra("reportList", mReportArrayList)
            startActivity(intent)
        }
        mListView = findViewById(R.id.listView)
        mListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(applicationContext, ReportDetailActivity::class.java)
            intent.putExtra("report", mReportArrayList[position])
            startActivity(intent)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onResume() {
        super.onResume()

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser

        mReportRef = dataBaseReference.child(UsersPATH).child(user!!.uid)
        mReportRef.addListenerForSingleValueEvent(mmmEventListener)

        mReportArrayList = ArrayList<Report>()

//        mListView = findViewById(R.id.listView)
        mAdapter = ReportAdapter(this)
        mAdapter.setReportArrayList(mReportArrayList)
        mListView.adapter = mAdapter
    }
}
