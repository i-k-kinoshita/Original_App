package jp.original_app.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.Fragment
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.support.v4.app.ListFragment
import android.util.Log
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import jp.original_app.*
import jp.original_app.R
import kotlinx.android.synthetic.main.activity_graph.*
import kotlinx.android.synthetic.main.activity_graph.lineChart
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.fragment_graph.*

class GraphFragment : Fragment() {

    private lateinit var graphViewModel: GraphViewModel
    private lateinit var mReportArrayList: ArrayList<Report>
    private lateinit var mValues: MutableList<Entry>
    private lateinit var mDate:MutableList<String>

    private var mHandler = Handler()

    // スタイルとフォントファミリーの設定
    private var mTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    // データの個数
    private val chartDataCount = 3

    private val mEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val map = snapshot.value as Map<String, String>
            val userName = map["name"] ?: ""

            val dataBaseReference = FirebaseDatabase.getInstance().reference
            val user = FirebaseAuth.getInstance().currentUser
            val userRef = dataBaseReference.child(UsersPATH).child(user!!.uid).child(reportPATH)

            userRef.limitToFirst(7).orderByChild("orderCnt").addChildEventListener(object :
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

                    if(mReportArrayList.size == 7){
                        mHandler.post {
                            for(i in 0 until mReportArrayList.size){
                                val temperate = mReportArrayList[mReportArrayList.size - i - 1].temperature.substring(0,4)

                                val long5 = mReportArrayList[mReportArrayList.size - i - 1].date.substring(9,10)
                                val long4 = mReportArrayList[mReportArrayList.size - i - 1].date.substring(8,9)

                                if(long5 == "日"){
                                    val date = mReportArrayList[mReportArrayList.size - i - 1].date.substring(5,10)
                                    mDate.add(date)
                                }else{
                                    if(long4 == "日"){
                                        val date = mReportArrayList[mReportArrayList.size - i - 1].date.substring(5,9)
                                        mDate.add(date)
                                    }else{
                                        val date = mReportArrayList[mReportArrayList.size - i - 1].date.substring(5,11)
                                        mDate.add(date)
                                    }
                                }
                                mValues.add(Entry(i.toFloat(),temperate.toFloat()))
                            }
                            // グラフの設定
                            setupLineChart()
                            // データの設定
                            lineChart.data = lineData(chartDataCount)
                        }
                    }
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
        graphViewModel =
            ViewModelProviders.of(this).get(GraphViewModel::class.java)
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mActivity = activity as Main2Activity
        mActivity.createButton.setOnClickListener {
            val intent = Intent(context, ConditionSendActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        mValues = mutableListOf<Entry>()
        mDate = mutableListOf<String>()
        mReportArrayList = ArrayList<Report>()

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        val reportRef = dataBaseReference.child(UsersPATH).child(user!!.uid)
        reportRef.addListenerForSingleValueEvent(mEventListener)

        // 時間を開ける,Sleepさせる
//        Log.d("Kotlintest","Start")
//        Thread.sleep(1_000)
//        Log.d("Kotlintest","Stop")

    }
    // LineChart用のデータ作成
    private fun lineData(count: Int): LineData {

        // グラフのレイアウトの設定
        val yVals = LineDataSet(mValues, "体温").apply {
            axisDependency =  YAxis.AxisDependency.LEFT //”体温”の表示する位置
            color = Color.RED // グラフの線の色
            highLightColor = Color.YELLOW  // タップ時のハイライトカラー
            setDrawCircles(true) // 点を○で表示
            // setDrawFilled(true) // 領域塗り潰し
            setDrawCircleHole(true) // ○塗り潰し
            // 点の値非表示
            setDrawValues(true) // 点に値を表示
            // 線の太さ
            lineWidth = 4f
        }
        val data = LineData(yVals)

        return data
    }
    private fun setupLineChart(){
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            isScaleXEnabled = false // 拡大縮小可能
            setPinchZoom(false)
            setDrawGridBackground(false)
            animateY(2000, Easing.EaseInOutSine)

            //データラベルの表示
            legend.apply {
                form = Legend.LegendForm.LINE
                typeface = mTypeface
                textSize = 18f
                setGridBackgroundColor(3)
                textColor = Color.BLACK
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            //y軸右側の設定
            axisRight.isEnabled = false

            //X軸表示
            xAxis.apply {
                typeface = mTypeface
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.BLACK
                // 格子線を表示する
                textDirection
                valueFormatter = IndexAxisValueFormatter(mDate)
                setDrawGridLines(true)
                textSize = 10f
            }

            //y軸左側の表示
            axisLeft.apply {
                typeface = mTypeface
                textColor = Color.BLACK
                // 格子線を表示する
                setDrawGridLines(true)
            }
        }
    }
}