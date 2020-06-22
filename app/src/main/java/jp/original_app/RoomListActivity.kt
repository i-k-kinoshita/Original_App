package jp.original_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_room_list.*

class RoomListActivity : AppCompatActivity() {

    private lateinit var mRoomList: ArrayList<String>
    private lateinit var mAdapter: RoomAdapter
    private lateinit var mMutableMap: MutableMap<Int,ArrayList<String>>

    private var mCnt = -1

    private val roomEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val roomName = dataSnapshot.key ?: ""
            mRoomList.add(roomName)
            mAdapter.notifyDataSetChanged()

            val databaseReference = FirebaseDatabase.getInstance().reference
            val userReference = databaseReference.child(roomPATH).child(roomName).child(UsersPATH)
            userReference.addChildEventListener(object : ChildEventListener{
                val array = ArrayList<String>()

                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val userID = dataSnapshot.key ?: ""

                    if(array.size == 0){
                        mCnt++
                    }
                    array.add(userID)
                    mMutableMap[mCnt] = array
                }
                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                }
                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                }
                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
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
        mMutableMap = mutableMapOf<Int,ArrayList<String>>()

        val user = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().reference
        val roomReference = databaseReference.child(UsersPATH).child(user!!.uid).child(roomPATH)

        listView.setOnItemClickListener { parent, view, position, id ->

            val intent = Intent(applicationContext, MemberListActivity::class.java)
            intent.putExtra("member", mMutableMap[position])
            intent.putExtra("roomName", mRoomList[position])
            startActivity(intent)
        }
        listView.setOnItemLongClickListener { parent, view, position, id ->
            val roomUserRef = databaseReference.child(roomPATH).child(mRoomList[position]).child(UsersPATH)

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@RoomListActivity)

            builder.setTitle("削除")
            builder.setMessage(mRoomList[position] + "を退会しますか")

            builder.setPositiveButton("OK"){_, _ ->
                roomReference.child(mRoomList[position]).removeValue()
                roomUserRef.child(user!!.uid).removeValue()
                val roomName = mRoomList[position]

                val listView = findViewById<ListView>(R.id.listView)
                mCnt = -1
                mRoomList = arrayListOf<String>()
                roomReference.addChildEventListener(roomEventListener)
                mAdapter.setReportArrayList(mRoomList)
                listView.adapter = mAdapter

                Snackbar.make(listView, roomName + "から退会しました", Snackbar.LENGTH_LONG).show()
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()


            true
        }
    }

    override fun onResume() {
        super.onResume()

        val listView = findViewById<ListView>(R.id.listView)
        mRoomList = arrayListOf<String>()

        mCnt = -1

        val user = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().reference
        val roomReference = databaseReference.child(UsersPATH).child(user!!.uid).child(roomPATH)
        roomReference.addChildEventListener(roomEventListener)


        // ArrayAdapterの生成
        mAdapter = RoomAdapter(this)
        mAdapter.setReportArrayList(mRoomList)

        // ListViewに、生成したAdapterを設定
        listView.adapter = mAdapter
    }
}
