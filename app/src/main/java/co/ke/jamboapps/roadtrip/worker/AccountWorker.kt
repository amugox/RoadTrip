package co.ke.jamboapps.roadtrip.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import co.ke.jamboapps.roadtrip.clazz.HttpClient
import co.ke.jamboapps.roadtrip.clazz.HttpReqActions
import co.ke.jamboapps.roadtrip.db.User
import org.json.JSONException
import org.json.JSONObject

class AccountWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            val accountAction = inputData.getInt("acc_action", 0)
            var deviceId: String? = ""
            val reqType = when (accountAction) {
                1 -> HttpReqActions.ACCOUNT_REGISTER
                else -> {
                    deviceId = inputData.getString("dev_id")
                    HttpReqActions.ACCOUNT_CREATE
                }
            }

            val httpClient = HttpClient()
            httpClient.setOnClientEventsListener(object : HttpClient.ClientEventsListener {
                override fun onSuccess(response: JSONObject) {
                    try {
                        val stat = response.getInt("stat")
                        if (stat == 0) {
                            //---- Process data
                            val data = response.getJSONObject("data")
                            when (accountAction) {
                                0 -> {

                                    val accountCode = data.getInt("acc_code")
                                    val accountId = data.getString("acc_id")

                                    User.create(accountId, accountCode, deviceId!!)
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: HttpClient.HttpError) {
                    error.exception.printStackTrace()
                }

                override fun onNoConnection() {}
            })

            val data: MutableMap<String, String> = HashMap()
            data["dev_id"] = deviceId!!
            httpClient.post(reqType, data)

            return Result.success(inputData)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}