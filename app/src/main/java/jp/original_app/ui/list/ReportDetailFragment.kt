package jp.original_app.ui.list

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.original_app.*
import jp.original_app.R

import kotlinx.android.synthetic.main.report_detail_fragment.*
import kotlinx.android.synthetic.main.report_detail_fragment.condition
import kotlinx.android.synthetic.main.report_detail_fragment.date
import kotlinx.android.synthetic.main.report_detail_fragment.remark
import kotlinx.android.synthetic.main.report_detail_fragment.temperature

class ReportDetailFragment : Fragment() {
    private val args: ReportDetailFragmentArgs by navArgs()
    private lateinit var mReport: Report
    private lateinit var mReportRef: DatabaseReference
    private var mUserName = ""
    private lateinit var viewModel: ReportDetailViewModel

    companion object {
        fun newInstance() = ReportDetailFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.report_detail_fragment, container, false)
    }

    private var reportListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val map = dataSnapshot.value as Map<String, String>
            date.text = dataSnapshot.key ?: ""
            temperature.text = map["temperature"] ?: ""
            condition.text = map["condition"] ?: ""
            remark.text = map["remark"] ?: ""

        }
        override fun onCancelled(databaseError: DatabaseError) {
        }
    }
    private var postListener: ValueEventListener = object : ValueEventListener {
        @SuppressLint("RestrictedApi")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val map = dataSnapshot.value as Map<String, String>
            mUserName = map["name"] ?: ""

            if(mReport.name == mUserName){
                delete_button.visibility = View.VISIBLE
                edit_button.visibility = View.VISIBLE
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference

        delete_button.setOnClickListener {
            // ダイアログを表示する
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("削除")
            builder.setMessage(mReport.date + "を削除しますか")
            builder.setPositiveButton("OK"){_, _ ->
                mReportRef.removeValue()
                findNavController().navigate(R.id.navigation_list)
                Toast.makeText(context,mReport.date + "を削除しました", Toast.LENGTH_SHORT).show();
            }
            builder.setNegativeButton("CANCEL", null)
            val dialog = builder.create()
            dialog.show()

            true
        }
        edit_button.setOnClickListener {
            val action = ReportDetailFragmentDirections.actionNavigationDetailReportToNavigationEditReport(mReport)
            findNavController().navigate(action)
        }

        edit_button.visibility = View.GONE
        delete_button.visibility = View.GONE

        val userRef = dataBaseReference.child(UsersPATH).child(user!!.uid)
        userRef.addListenerForSingleValueEvent(postListener)
    }

    override fun onResume() {
        super.onResume()
        mReport = args.report
        val user = FirebaseAuth.getInstance().currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mReportRef = dataBaseReference.child(UsersPATH).child(mReport.userUid).child(reportPATH).child(mReport.date)
        mReportRef.addListenerForSingleValueEvent(reportListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReportDetailViewModel::class.java)
    }
}
