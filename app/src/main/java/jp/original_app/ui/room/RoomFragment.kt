package jp.original_app.ui.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import jp.original_app.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_room_list.*

class RoomFragment : Fragment() {

    private lateinit var roomViewModel: RoomViewModel
    private lateinit var mRoomList: ArrayList<String>
    private lateinit var mAdapter: RoomAdapter
    private lateinit var mMutableMap: MutableMap<Int,ArrayList<String>>

    private var mCnt = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        roomViewModel =
            ViewModelProviders.of(this).get(RoomViewModel::class.java)
        return inflater.inflate(R.layout.fragment_room, container, false)
    }

    private val roomEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val roomName = dataSnapshot.key ?: ""
            mRoomList.add(roomName)
            mAdapter.notifyDataSetChanged()

            val databaseReference = FirebaseDatabase.getInstance().reference
            val userReference = databaseReference.child(roomPATH).child(roomName).child(UsersPATH)
            userReference.addChildEventListener(object : ChildEventListener {
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mActivity = activity as Main2Activity
        mActivity.createButton.setOnClickListener {
            val intent = Intent(context, CreateRoomActivity::class.java)
            startActivity(intent)
        }

        mRoomList = arrayListOf<String>()
        mMutableMap = mutableMapOf<Int,ArrayList<String>>()

        val user = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().reference
        val roomReference = databaseReference.child(UsersPATH).child(user!!.uid).child(roomPATH)

        listView.setOnItemClickListener { parent, view, position, id ->
            val room = mMutableMap[position]?.let { Room(mRoomList[position], it) }
            val action = RoomFragmentDirections.actionNavigationRoomToNavigationListMember(room!!)
            findNavController().navigate(action)

        }
        listView.setOnItemLongClickListener { parent, view, position, id ->
            val roomUserRef = databaseReference.child(roomPATH).child(mRoomList[position]).child(UsersPATH)

            // ダイアログを表示する
            val builder = AlertDialog.Builder(context!!)

            builder.setTitle("削除")
            builder.setMessage(mRoomList[position] + "を退会しますか")

            builder.setPositiveButton("OK"){_, _ ->
                roomReference.child(mRoomList[position]).removeValue()
                roomUserRef.child(user!!.uid).removeValue()
                val roomName = mRoomList[position]

                val listView = view.findViewById<ListView>(R.id.listView)
                mCnt = -1
                mRoomList = arrayListOf<String>()
                roomReference.addChildEventListener(roomEventListener)
                mAdapter.setReportArrayList(mRoomList)
                listView.adapter = mAdapter
                Toast.makeText(context, roomName + "から退会しました", Toast.LENGTH_SHORT).show();
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }


    }

    override fun onResume() {
        super.onResume()

        val listView = view!!.findViewById<ListView>(R.id.listView)
        mRoomList = arrayListOf<String>()

        mCnt = -1

        val user = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().reference
        val roomReference = databaseReference.child(UsersPATH).child(user!!.uid).child(roomPATH)
        roomReference.addChildEventListener(roomEventListener)

        // ArrayAdapterの生成
        mAdapter = RoomAdapter(context!!)
        mAdapter.setReportArrayList(mRoomList)

        // ListViewに、生成したAdapterを設定
        listView.adapter = mAdapter

    }
}