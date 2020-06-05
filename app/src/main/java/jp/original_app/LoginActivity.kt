package jp.original_app

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
import kotlinx.android.synthetic.main.activity_login.*
import android.app.AlarmManager
import android.support.v4.content.ContextCompat.getSystemService
import android.app.PendingIntent
import java.util.*





const val EXTRA_ORIGINAL = "jp.original_app"


class LoginActivity : AppCompatActivity() {

    // プロパティ
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()

        // ログイン処理のリスナー
        mLoginListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功した場合
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)

            } else {
                // 失敗した場合、エラーを表示する
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, "ログインに失敗しました", Snackbar.LENGTH_LONG).show()
            }
        }

        loginButton.setOnClickListener { v ->

            // ログインスキップ
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            // ↑ここまで

            // キーボードが出てたら閉じる
//            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
//
//            val email = emailText.text.toString()
//            val password = passwordText.text.toString()
//
//            if (email.isNotEmpty() && password.length >= 6) {
//                login(email, password)
//
//            } else {
//                // エラーを表示する
//                Snackbar.make(v, "正しく入力してください", Snackbar.LENGTH_LONG).show()
//            }
        }

        createButton.setOnClickListener {
            val intent = Intent(applicationContext, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        // TODO プレファレンス

        // 通知機能
        // AlarmManagerから通知を受け取るレシーバーを定義する
        val intent = Intent(applicationContext, AlarmReceiver::class.java)
        // レシーバーで判断するため 適当でOK
        intent.putExtra("intent_alarm_id_key", 1000)

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
    }
    // 処理中のダイアログを表示してFirebaseにログインを指示。
    private fun login(email: String, password: String) {
        // ログインする
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener)
    }
}
