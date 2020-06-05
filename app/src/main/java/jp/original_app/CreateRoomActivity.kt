package jp.original_app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_room.*
import kotlinx.android.synthetic.main.activity_create_room.passwordText
import java.text.SimpleDateFormat
import java.util.*

class CreateRoomActivity : AppCompatActivity() , View.OnClickListener {

    private var mPassword = ""
    private var mRoomName = ""
    private lateinit var mData: HashMap<String,String>
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mRoomReference: DatabaseReference
    private lateinit var mUserReference: DatabaseReference

    private val roomEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if(snapshot.value != null) {
                val map = snapshot.value as Map<String, String>
                val password = map["password"] ?: ""
                if(password == mPassword){
                    val user = FirebaseAuth.getInstance().currentUser
                    mUserReference.child(mRoomName).setValue(mData)
                    mRoomReference.child(mRoomName).child(UsersPATH).child(user!!.uid).setValue(mData)
                    finish()
                }else{
                    val view = findViewById<View>(android.R.id.content)
                    Snackbar.make(view, "パスワードが正しくありません", Snackbar.LENGTH_LONG).show()
                }
            }else{
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, "グループが存在しません", Snackbar.LENGTH_LONG).show()
            }
        }
        override fun onCancelled(firebaseError: DatabaseError) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)

        val user = FirebaseAuth.getInstance().currentUser
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        mRoomReference = mDatabaseReference.child(roomPATH)
        mUserReference = mDatabaseReference.child(UsersPATH).child(user!!.uid).child(roomPATH)
        mData = HashMap<String, String>()

        createRoom.setOnClickListener(this)
        loginRoom.setOnClickListener(this)
    }
    override fun onClick(v: View) {
        mRoomName = roomName.text.toString()
        mPassword = passwordText.text.toString()

        // 現在時刻の取得
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyMMdd")
        val date = sdf.format(cal.time)
        mData["date"] = date

        if (mRoomName.isNotEmpty() && mPassword.length >= 6) {
            if(v.id == R.id.createRoom){
                val data = HashMap<String, String>()
                val user = FirebaseAuth.getInstance().currentUser
                data["password"] = mPassword
                data["manager"] = user!!.uid

                mRoomReference.child(mRoomName).setValue(data)
                mRoomReference.child(mRoomName).child(UsersPATH).child(user!!.uid).setValue(mData)
                mUserReference.child(mRoomName).setValue(mData)
                finish()

            }else if(v.id == R.id.loginRoom){
                mRoomReference.child(mRoomName).addListenerForSingleValueEvent(roomEventListener)
            }
        }else{
            // エラーを表示する
            Snackbar.make(v, "正しく入力してください", Snackbar.LENGTH_LONG).show()
        }
    }
}
