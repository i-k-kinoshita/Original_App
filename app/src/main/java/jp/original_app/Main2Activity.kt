package jp.original_app

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main2.*
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.util.Log


class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)




//        createButton.setOnClickListener {
////            Snackbar.make(it, "ボタンが押されました", Snackbar.LENGTH_LONG).show()
//            val intent = Intent(applicationContext, ConditionSendActivity::class.java)
//            startActivity(intent)
//
//
//
//            val fragManager = this.supportFragmentManager
//            val count = this.supportFragmentManager.backStackEntryCount
//            val frag = fragManager.fragments[if (count > 0) count - 1 else count]
//
//            Log.d("Kotlintest","$frag")
//
//
////        }
//        roomLoginButton.setOnClickListener {
//            val intent = Intent(applicationContext, CreateRoomActivity::class.java)
//            startActivity(intent)
//        }
    }

}
