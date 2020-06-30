package jp.original_app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.view.View
import android.widget.SpinnerAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_condition_send.*
import java.util.*
import java.text.SimpleDateFormat

class ConditionSendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_condition_send)

        // 現在時刻の取得
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy年M月dd日(E)")
        val sdf2 = SimpleDateFormat("yyMMdd")
        val date = sdf.format(cal.time)
        val cntData = sdf2.format(cal.time)


//        val user = FirebaseAuth.getInstance().currentUser
//        val databaseReference = FirebaseDatabase.getInstance().reference

//        val random = Random()
//
//        for(i in 10 until 25){
//            var date = ""
//            val judg = i % 7
//            if(judg == 0){
//                date = "2020年6月" + ( i + 1 ) + "日(月)"
//
//            }else if(judg == 1){
//                date = "2020年6月" + ( i + 1 ) + "日(火)"
//
//            }else if(judg == 2){
//                date = "2020年6月" + ( i + 1 ) + "日(水)"
//
//            }else if(judg == 3){
//                date = "2020年6月" + ( i + 1 ) + "日(木)"
//
//            }else if(judg == 4){
//                date = "2020年6月" + ( i + 1 ) + "日(金)"
//
//            }else if(judg == 5){
//                date = "2020年6月" + ( i + 1 ) + "日(土)"
//
//            }else if(judg == 6){
//                date = "2020年6月" + ( i + 1 ) + "日(日)"
//
//            }
//
//            val randomData = random.nextFloat()
//            val temperature = 35.8 + randomData*1.8
//            val temperatureData = temperature.toString().substring(0,4) + "℃"
//            var condition = "良い"
//            var remark = ""
//
//            val oderCnt = "202006$i"
//            val cnt = 1/oderCnt.toFloat()
//
//            if(temperature in 35.8..36.3){
//                condition = "普通"
//            }else if(temperature > 36.3 && temperature <= 36.8){
//                condition = "良い"
//            }else{
//                condition = "悪い"
//                remark = "花粉症"
//            }
//
//
//            Log.d("Kotlintest","$date")
//            Log.d("Kotlintest","$temperatureData")
//            Log.d("Kotlintest","$condition")
//            Log.d("Kotlintest","$remark")
//            Log.d("Kotlintest","$cnt")
//            Log.d("Kotlintest","---------------------")
//
//            val reportReference = databaseReference.child(UsersPATH).child(user!!.uid).child(reportPATH).child(date)
//
//            val data = HashMap<String, String>()
//
//            data["temperature"] = temperatureData
//            data["condition"] = condition
//            data["remark"] = remark
//            data["orderCnt"] = cnt.toString()
//
//            reportReference.setValue(data)
//
//        }

        day.text = date

        var temperaturelist = listOf("35.5℃","35.6℃","35.7℃","35.8℃","35.9℃","36.0℃"
            ,"36.1℃","36.2℃","36.3℃","36.4℃","36.5℃","36.6℃","36.7℃","36.8℃","36.9℃","37.0℃"
            ,"37.1℃","37.2℃","37.3℃","37.4℃","37.5℃","37.6℃","37.7℃","37.8℃","37.9℃","38.0℃"
            ,"38.1℃","38.2℃","38.3℃","38.4℃","38.5℃","38.6℃","38.7℃","38.8℃","38.9℃","39.0℃")
        var conditionlist = listOf("良い","普通","悪い")

        //アダプターを設定
        val temperatureadapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            temperaturelist
        )
        val conditionadapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            conditionlist
        )
        temperature_spinner.adapter = temperatureadapter as SpinnerAdapter?
        temperature_spinner.setSelection(10)
        condition_spinner.adapter = conditionadapter
        condition_spinner.setSelection(1)

        back_button.setOnClickListener {
            finish()
        }

        send_button.setOnClickListener{
            val temperature = temperature_spinner.selectedItem.toString()
            val condition = condition_spinner.selectedItem.toString()
            val remark = remark_editText.text.toString()

            val user = FirebaseAuth.getInstance().currentUser
            val databaseReference = FirebaseDatabase.getInstance().reference
            val reportReference = databaseReference.child(UsersPATH).child(user!!.uid).child(reportPATH).child(date)

            val data = HashMap<String, String>()

            val cnt = 1/cntData.toFloat()

            data["temperature"] = temperature
            data["condition"] = condition
            data["remark"] = remark
            data["orderCnt"] = cnt.toString()

            reportReference.setValue(data)

            val view = findViewById<View>(android.R.id.content)
            Toast.makeText(applicationContext, "登録が完了しました", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

}
