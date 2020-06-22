package jp.original_app

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.activity_graph.*

class GraphActivity : AppCompatActivity() {

    private lateinit var mReportArrayList: ArrayList<Report>
    private lateinit var mValues: MutableList<Entry>
    private lateinit var mDate:MutableList<String>

    // スタイルとフォントファミリーの設定
    private var mTypeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    // データの個数
    private val chartDataCount = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        mValues = mutableListOf<Entry>()
        mDate = mutableListOf<String>()

        val extras = intent.extras
        mReportArrayList = extras.get("reportList") as ArrayList<Report>

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
//        lineChart.data = lineData(chartDataCount, 100f)
        lineChart.data = lineData(chartDataCount)
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