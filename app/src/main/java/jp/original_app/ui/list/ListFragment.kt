package jp.original_app.ui.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ListView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.original_app.*
import jp.original_app.R
import kotlinx.android.synthetic.main.activity_main2.*


class ListFragment : Fragment() {

    private lateinit var listViewModel: ListViewModel
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

            userRef.limitToFirst(14).orderByChild("orderCnt").addChildEventListener(object :
                ChildEventListener {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listViewModel =
            ViewModelProviders.of(this).get(ListViewModel::class.java)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mActivity = activity as Main2Activity
        mActivity.createButton.setOnClickListener {
            val intent = Intent(context, ConditionSendActivity::class.java)
            startActivity(intent)
        }

        mListView = view!!.findViewById(R.id.listView)

        mListView.setOnItemClickListener { parent, view, position, id ->

            val report = mReportArrayList[position]

            // 生成されたクラスに引数を渡して遷移
            val action = ListFragmentDirections.actionNavigationListToNavigationDetailReport(report)
            findNavController().navigate(action)

        }

    }
    override fun onResume() {
        super.onResume()
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser

        mReportRef = dataBaseReference.child(UsersPATH).child(user!!.uid)
        mReportRef.addListenerForSingleValueEvent(mmmEventListener)

        mReportArrayList = ArrayList<Report>()

        mAdapter = ReportAdapter(context!!)
        mAdapter.setReportArrayList(mReportArrayList)
        mListView.adapter = mAdapter
    }
}