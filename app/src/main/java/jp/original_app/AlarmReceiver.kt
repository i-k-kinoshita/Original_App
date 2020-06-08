package jp.original_app

import android.annotation.SuppressLint
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.app.NotificationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var mContext: Context
    private lateinit var mIntent: Intent

    private var postListener: ValueEventListener = object : ValueEventListener {
        @SuppressLint("RestrictedApi")
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if(dataSnapshot.value == null) {
                val messageId = mIntent.getIntExtra("intent_alarm_id_key", 0)
                val builder = NotificationCompat.Builder(mContext, "default")

                builder.setSmallIcon(R.drawable.small_icon)
                builder.setLargeIcon(BitmapFactory.decodeResource(mContext.resources, R.drawable.large_icon))
                builder.setWhen(System.currentTimeMillis())
                builder.setDefaults(Notification.DEFAULT_ALL)
                builder.setAutoCancel(true)

                // 情報を設定する
                builder.setTicker("体調管理アプリ")   // 5.0以降は表示されない
                builder.setContentTitle("体調管理アプリ")
                builder.setSubText("体調管理アプリ")
                builder.setContentText("本日の体調を登録しましょう！")

                // 通知をタップしたらアプリを起動する
                val startAppIntent = Intent(mContext, LoginActivity::class.java)
                startAppIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
                val pendingIntent = PendingIntent.getActivity(mContext, 0, startAppIntent, 0)
                builder.setContentIntent(pendingIntent)

                val manager = mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                manager.notify(0, builder.build())
            }
        }
        override fun onCancelled(databaseError: DatabaseError) {
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        mContext = context
        mIntent = intent

        // 現在時刻の取得
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy年M月dd日(E)")
        val date = sdf.format(cal.time)

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        val reportRef = dataBaseReference.child(UsersPATH).child(user!!.uid).child(reportPATH).child(date)

        reportRef.addListenerForSingleValueEvent(postListener)
    }
}