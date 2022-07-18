package co.ke.jamboapps.roadtrip

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.work.*
import co.ke.jamboapps.roadtrip.db.User
import co.ke.jamboapps.roadtrip.util.AppUtil
import co.ke.jamboapps.roadtrip.worker.AccountWorker
import com.google.android.gms.tasks.Task
import com.google.firebase.installations.FirebaseInstallations

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        supportActionBar?.hide()

        findViewById<TextView>(R.id.tv_version).text =
            getString(R.string.tv_version, AppUtil.getAppVersion())

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            //--- Get user
            val user = User.get()
            if (user == null) {
                //---- Get device id
                FirebaseInstallations.getInstance().id.addOnCompleteListener { task: Task<String?> ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.e("TOKEN ---->>", token!!)
                        registerAccount(token)
                    }
                    startApp()
                }
            } else {
                Log.e("USER ID", user.userId)
                startApp()
            }

        }, 2500)
    }

    private fun startApp() {
        val intent = Intent(applicationContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerAccount(devId: String) {
        val workerData = workDataOf("acc_action" to 0, "dev_id" to devId)
        val workReq: WorkRequest =
            OneTimeWorkRequestBuilder<AccountWorker>().setInputData(workerData).build()

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workReq.id)
//            .observe(this as LifecycleOwner, Observer { workInfo ->
//
//        })
        WorkManager.getInstance(this).enqueue(workReq)
    }
}