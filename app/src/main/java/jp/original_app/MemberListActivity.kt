package jp.original_app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MemberListActivity : AppCompatActivity() {

    private lateinit var mAdapter: MemberAdapter
    private lateinit var mListView: ListView
    private lateinit var mMemberArrayList: ArrayList<String>
    private lateinit var mReportArrayList: ArrayList<Report>
    private lateinit var mReportRef: DatabaseReference





    private val memberEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val userID = dataSnapshot.key ?: ""
            mMemberArrayList.add(userID)

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

    private val mEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val test = snapshot.key ?: ""
            Log.d("Kotlintest","lll$test")

        }
        override fun onCancelled(firebaseError: DatabaseError) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_list)

        val extras = intent.extras
        mMemberArrayList = extras.get("member") as ArrayList<String>

        mReportArrayList = ArrayList<Report>()

        Log.d("Kotlintest","member"+mMemberArrayList.size.toString())

        for(i in 0 until mMemberArrayList.size){
            val test =  mMemberArrayList[i]
            Log.d("Kotlintest","$test")
        }


//        val user = FirebaseAuth.getInstance().currentUser
//        val databaseReference = FirebaseDatabase.getInstance().reference
//        val userReference = databaseReference.child(roomPATH).child(roomName).child(UsersPATH)
//        userReference.addChildEventListener(memberEventListener)
//
//
//
//        val i = mMemberArrayList.size
//
//        Log.d("Kotlintest",i.toString())
//
//        for(i in 0 until mMemberArrayList.size){
//            mReportRef = databaseReference.child(UsersPATH).child(mMemberArrayList[i])
//            mReportRef.addListenerForSingleValueEvent(mEventListener)
//
//            Log.d("Kotlintest","---------------")
//
//        }




//        mListView = findViewById(R.id.listView)



    }

    override fun onResume() {
        super.onResume()

//        mAdapter = MemberAdapter(this)
//        mAdapter.setReportArrayList(mMemberArrayList)
//        mListView.adapter = mAdapter
    }
}
