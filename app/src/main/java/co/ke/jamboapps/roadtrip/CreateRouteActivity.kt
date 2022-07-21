package co.ke.jamboapps.roadtrip

import android.os.Bundle
import android.text.TextUtils
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import co.ke.jamboapps.roadtrip.clazz.HttpClient
import co.ke.jamboapps.roadtrip.clazz.HttpReqActions
import co.ke.jamboapps.roadtrip.databinding.ActivityCreateRouteBinding
import co.ke.jamboapps.roadtrip.db.MyRoute
import co.ke.jamboapps.roadtrip.dialog.Alerts
import co.ke.jamboapps.roadtrip.dialog.WaitDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CreateRouteActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateRouteBinding
    private var wd: WaitDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupHomeButton()

        wd = WaitDialog(this)

        binding.contentView.btnSubmit.setOnClickListener { onSubmit() }

    }

    private fun onSubmit() {
        if (TextUtils.isEmpty(binding.contentView.etName.text)) {
            binding.contentView.etName.error = getString(R.string.err_empty_input)
            binding.contentView.etName.requestFocus()
            return
        }
        val routeName = binding.contentView.etName.text.toString()

        if (TextUtils.isEmpty(binding.contentView.etDesc.text)) {
            binding.contentView.etDesc.error = getString(R.string.err_empty_input)
            binding.contentView.etDesc.requestFocus()
            return
        }
        val routeDesc = binding.contentView.etDesc.text.toString()

        val httpClient = HttpClient(this)
        httpClient.setOnClientEventsListener(object : HttpClient.ClientEventsListener {
            override fun onSuccess(response: JSONObject) {
                wd?.dismiss()
                try {
                    val stat = response.getInt("stat")
                    if (stat == 0) {
                        //---- Process data
                        val data = response.getJSONObject("data")
                        val routeId = data.getString("rt_id")
                        val routeCode = data.getInt("rt_code")

                        val route = MyRoute()
                        route.routeId = routeId
                        route.code = routeCode
                        route.routeName = routeName
                        route.details = routeDesc
                        route.createdOn = Calendar.getInstance().time
                        route.myRoute = 1
                        route.save()

                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Alerts.showWarningMessage(
                            this@CreateRouteActivity,
                            response.getString("msg")
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Alerts.showWarningMessage(
                        this@CreateRouteActivity,
                        R.string.err_processing_http_response
                    )
                }
            }

            override fun onError(error: HttpClient.HttpError) {
                wd?.dismiss()
                error.exception.printStackTrace()
                Alerts.showWarningMessage(this@CreateRouteActivity, error.message)
            }

            override fun onNoConnection() {
                wd?.dismiss()
            }
        })

        //--- Post to server

        val data: MutableMap<String, String> = HashMap()
        data["name"] = routeName
        data["desc"] = routeDesc

        wd?.show()
        httpClient.post(HttpReqActions.ROUTE_CREATE, data)
    }
}