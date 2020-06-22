package jp.original_app.ui.notice

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.original_app.ConditionSendActivity
import jp.original_app.CreateRoomActivity
import jp.original_app.Main2Activity

import jp.original_app.R
import kotlinx.android.synthetic.main.activity_main2.*

class NoticeFragment : Fragment() {

    companion object {
        fun newInstance() = NoticeFragment()
    }

    private lateinit var viewModel: NoticeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notice, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mActivity = activity as Main2Activity
        mActivity.createButton.setOnClickListener {
            val intent = Intent(context, ConditionSendActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NoticeViewModel::class.java)
        // Use the ViewModel
    }

}
