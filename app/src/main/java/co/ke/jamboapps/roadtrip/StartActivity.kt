package co.ke.jamboapps.roadtrip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import co.ke.jamboapps.roadtrip.util.AppUtil

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        supportActionBar?.hide()

        findViewById<TextView>(R.id.tv_version).text =
            getString(R.string.tv_version, AppUtil.getAppVersion())

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            //startService();

            val intent = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intent)
            finish()

        }, 2500)
    }
}