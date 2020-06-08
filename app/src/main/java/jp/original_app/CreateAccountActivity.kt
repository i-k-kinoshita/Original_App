package jp.original_app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_create_account.createButton
import kotlinx.android.synthetic.main.activity_create_account.emailText
import kotlinx.android.synthetic.main.activity_create_account.passwordText
import java.util.*

class CreateAccountActivity : AppCompatActivity() {

    // プロパティ
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCreateAccountListener: OnCompleteListener<AuthResult>
    private lateinit var mDataBaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        mDataBaseReference = FirebaseDatabase.getInstance().reference

        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()

        // アカウント作成処理のリスナー
        mCreateAccountListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                val user = mAuth.currentUser
                val userRef = mDataBaseReference.child(UsersPATH).child(user!!.uid)

                // アカウント作成の時は表示名をFirebaseに保存する
                val name = nameText.text.toString()

                val data = HashMap<String, String>()
                data["name"] = name
                userRef.setValue(data)




                // 通知機能
                // AlarmManagerから通知を受け取るレシーバーを定義する
                // 実行したいクラスから Intent を作成
                val intent = Intent(applicationContext, AlarmReceiver::class.java)
                // レシーバーで判断するため 適当でOK
                intent.putExtra("intent_alarm_id_key", 0)

                val sender = PendingIntent.getBroadcast(
                    applicationContext, 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                // 通知させたい時間をCalendarを使って定義する
                val calSet = Calendar.getInstance()
                calSet.timeInMillis = System.currentTimeMillis()
                calSet.timeZone = TimeZone.getDefault()

                // 毎日9:00に通知を表示させる
                calSet.set(Calendar.HOUR_OF_DAY, 9)
                calSet.set(Calendar.MINUTE,0)

                val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                // AlarmManager.RTC_WAKEUPで端末スリープ時に起動させるようにする
                // 1回だけ通知の場合はalarmManager.set()を使う
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP, calSet.timeInMillis,
                    // 一日毎にアラームを呼び出す
                    AlarmManager.INTERVAL_DAY, sender
                )
                // ここまで通知機能





                finish()

            } else {
                // 失敗した場合
                // エラーを表示する
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, "アカウント作成に失敗しました", Snackbar.LENGTH_LONG).show()

            }
        }

        createButton.setOnClickListener { v ->
            // キーボードが出てたら閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

            val email = emailText.text.toString()
            val password = passwordText.text.toString()
            val name = nameText.text.toString()

            if (email.length != 0 && password.length >= 6 && name.length != 0) {
                createAccount(email, password)

            } else {
                // エラーを表示する
                Snackbar.make(v, "正しく入力してください", Snackbar.LENGTH_LONG).show()
            }
        }
    }
    // 処理中のダイアログを表示してFirebaseにアカウント作成を指示。
    private fun createAccount(email: String, password: String) {
        // アカウントを作成する
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(mCreateAccountListener)
    }
}
