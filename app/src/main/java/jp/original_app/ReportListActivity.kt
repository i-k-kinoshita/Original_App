package jp.original_app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ReportListActivity : AppCompatActivity() {

    private lateinit var mReportArrayList: ArrayList<Report>
    private lateinit var mAdapter: ReportAdapter
    private lateinit var mListView: ListView
    private lateinit var mReportRef: DatabaseReference

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val date = dataSnapshot.key ?: ""
            val map = dataSnapshot.value as Map<String, String>

            val temperature = map["temperature"] ?: ""
            val condition = map["condition"] ?: ""
            val remark = map["remark"] ?: ""

            val report = Report(date, temperature, condition, remark)
            mReportArrayList.add(report)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_condition)


        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        mReportRef = dataBaseReference.child(UsersPATH).child(user!!.uid).child(reportPATH)
        mReportRef.addChildEventListener(mEventListener)

        mReportArrayList = ArrayList<Report>()

        mListView = findViewById(R.id.listView)
        mAdapter = ReportAdapter(this)
        mAdapter.setReportArrayList(mReportArrayList)
        mListView.adapter = mAdapter

    }
}
