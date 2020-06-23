package jp.original_app.ui.room

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.original_app.*
import jp.original_app.R
import jp.original_app.ui.list.ListFragmentDirections

import jp.original_app.ui.list.ReportDetailFragmentArgs
import kotlinx.android.synthetic.main.activity_create_room.view.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MemberListFragment : Fragment() {
    private val args: MemberListFragmentArgs by navArgs()
    private lateinit var mAdapter: MemberAdapter
    private lateinit var mListView: ListView
    private lateinit var mMemberArrayList: ArrayList<String>
    private lateinit var mReportArrayList: ArrayList<Report>
    private lateinit var mReportRef: DatabaseReference
    private var mRoomName = ""
    private var mManagerUid = ""


    companion object {
        fun newInstance() = MemberListFragment()
    }

    private lateinit var viewModel: MemberListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.member_list_fragment, container, false)
    }

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



    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val room = args.room
        mMemberArrayList = room.userUid
        mRoomName = room.roomName

        mReportArrayList = ArrayList<Report>()
        mListView = view.findViewById(R.id.listView)

        val user = FirebaseAuth.getInstance().currentUser
        val databaseReference = FirebaseDatabase.getInstance().reference
        val managerRef = databaseReference.child(roomPATH).child(mRoomName)
        val userRef = databaseReference.child(UsersPATH)
        managerRef.addListenerForSingleValueEvent(managerListener)

        mListView.setOnItemLongClickListener { parent, view, position, id ->
            val report = parent.adapter.getItem(position) as Report

            if(user!!.uid == mManagerUid){
                if(report.userUid == user!!.uid){
                    val intent = Intent(context, ReportDetailActivity::class.java)
                    intent.putExtra("report", mReportArrayList[position])
                    startActivity(intent)

                }else{
                    // 他人なら
                    // ダイアログを表示する
                    val builder = AlertDialog.Builder(context!!)

                    builder.setTitle("削除")
                    builder.setMessage(report.name + "さんを退会させますか")

                    builder.setPositiveButton("OK"){_, _ ->
                        managerRef.child(UsersPATH).child(report.userUid).removeValue()
                        userRef.child(report.userUid).child(roomPATH).child(mRoomName).removeValue()

                        Toast.makeText(context, report.name + "さんを退会させました", Toast.LENGTH_SHORT).show();
                        fragmentManager!!.popBackStack()
                    }

                    builder.setNegativeButton("CANCEL", null)

                    val dialog = builder.create()
                    dialog.show()

                }
            }else{
                // 管理者でなければ
                val report = mReportArrayList[position]

                // 生成されたクラスに引数を渡して遷移
                val action = MemberListFragmentDirections.actionNavigationListMemberToNavigationDetailReport(report)
                findNavController().navigate(action)
            }
            true
        }
        mListView.setOnItemClickListener { parent, view, position, id ->
            val report = mReportArrayList[position]

            // 生成されたクラスに引数を渡して遷移
            val action = MemberListFragmentDirections.actionNavigationListMemberToNavigationDetailReport(report)
            findNavController().navigate(action)
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

        mAdapter = MemberAdapter(context!!)
        mAdapter.setReportArrayList(mReportArrayList)
        mListView.adapter = mAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MemberListViewModel::class.java)
        // Use the ViewModel
    }

}
