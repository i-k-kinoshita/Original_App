package jp.original_app

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.app.NotificationManager
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Log.d("Kotlintest","receive")

        // TODO データを登録していると以下の処理を飛ばす

        val messageId = intent.getIntExtra("intent_alarm_id_key", 0)

        val builder = NotificationCompat.Builder(context, "default")

        builder.setSmallIcon(R.drawable.small_icon)
        builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.large_icon))
        builder.setWhen(System.currentTimeMillis())
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setAutoCancel(true)

        // 情報を設定する
        builder.setTicker("体調管理アプリ")   // 5.0以降は表示されない
        builder.setContentTitle("体調管理アプリ")
        builder.setSubText("体調管理アプリ")
        builder.setContentText("本日の体調を登録しましょう！")

        // 通知をタップしたらアプリを起動する
        val startAppIntent = Intent(context, LoginActivity::class.java)
        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)
        val pendingIntent = PendingIntent.getActivity(context, 0, startAppIntent, 0)
        builder.setContentIntent(pendingIntent)

        val manager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        manager.notify(100, builder.build())
    }
}