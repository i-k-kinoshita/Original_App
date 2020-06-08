package jp.original_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_room_list.*

class RoomListActivity : AppCompatActivity() {

    private lateinit var mRoomList: ArrayList<String>
    private lateinit var mAdapter: ArrayAdapter<String>
    private lateinit var mMemberArrayList: ArrayList<String>



    private val roomEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val roomName = dataSnapshot.key ?: ""
            mRoomList.add(roomName)
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
    private val memberEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val userID = dataSnapshot.key ?: ""
            mMemberArrayList.add(userID)

            Log.d("Kotlintest","${mMemberArrayList.size}")

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
        setContentView(R.layout.activity_room_list)

        create.setOnClickListener {
            val intent = Intent(applicationContext, CreateRoomActivity::class.java)
            startActivity(intent)
        }

        mRoomList = arrayListOf<String>()
        mMemberArrayList = arrayListOf<String>()



        val user = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().reference
        val roomReference = databaseReference.child(UsersPATH).child(user!!.uid).child(roomPATH)
        roomReference.addChildEventListener(roomEventListener)

        val listView = findViewById<ListView>(R.id.listView)

        // ArrayAdapterの生成
        mAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mRoomList)

        // ListViewに、生成したAdapterを設定
        listView.adapter = mAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val databaseReference = FirebaseDatabase.getInstance().reference
            val userReference = databaseReference.child(roomPATH).child(mRoomList[position]).child(UsersPATH)
            userReference.addChildEventListener(memberEventListener)



            val intent = Intent(applicationContext, MemberListActivity::class.java)
            intent.putExtra("member", mMemberArrayList)
            startActivity(intent)
        }
    }
}
