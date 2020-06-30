package jp.original_app.ui.notice

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.original_app.*
import jp.original_app.R
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_notice.*

class NoticeFragment : Fragment() {

    companion object {
        fun newInstance() = NoticeFragment()
    }
    private lateinit var viewModel: NoticeViewModel
    private lateinit var mAdapter: RequestAdapter
    private lateinit var mListView: ListView
    private lateinit var mUserReference: DatabaseReference
    private lateinit var mRequestList: ArrayList<Request>
    private var mHandler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notice, container, false)
    }

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            if(dataSnapshot.value != null){
                val roomName = dataSnapshot.key ?: ""
                val databaseReference = FirebaseDatabase.getInstance().reference
                val userReference = databaseReference.child(roomPATH).child(roomName).child(requestPATH)
                userReference.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                        if(dataSnapshot.value != null){
                            val userUid = dataSnapshot.key ?: ""
                            val map = dataSnapshot.value as Map<String, String>
                            val userName = map["name"] ?: ""
                            val request = Request(userName,roomName,userUid)
                            mRequestList.add(request)
                            mAdapter.setRequestArrayList(mRequestList)
                            mListView.adapter = mAdapter
                            mHandler.post{
                                noticeText.visibility = GONE
                            }
                        }
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
            val intent = Intent(context, ConditionSendActivity::class.java)
            startActivity(intent)
        }
        mListView = view!!.findViewById(R.id.requestListView)
        mAdapter = RequestAdapter(context!!)
        mRequestList = ArrayList<Request>()
    }

    override fun onResume() {
        super.onResume()

        noticeText.visibility = VISIBLE
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        mUserReference = dataBaseReference.child(UsersPATH).child(user!!.uid).child(myRoomPATH)
        mUserReference.addChildEventListener(mEventListener)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NoticeViewModel::class.java)
    }
}
