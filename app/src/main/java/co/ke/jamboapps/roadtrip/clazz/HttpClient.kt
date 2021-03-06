package co.ke.jamboapps.roadtrip.clazz

import android.app.Activity
import android.content.Context
import android.util.Log
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.app.App
import co.ke.jamboapps.roadtrip.app.AppConfig
import co.ke.jamboapps.roadtrip.db.User
import co.ke.jamboapps.roadtrip.dialog.Alerts
import co.ke.jamboapps.roadtrip.util.AppUtil
import com.android.volley.*
import com.android.volley.Request.Method.POST
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class HttpClient(activity: Activity? = null) {

    interface ClientEventsListener {
        fun onSuccess(response: JSONObject)

        fun onError(error: HttpError)

        fun onNoConnection()
    }

    data class HttpError(var errorNo: Int, var message: String, var exception: Exception)

    private var listener: ClientEventsListener? = null
    private val context: Context = App.getAppInstance().applicationContext
    private var myActivity: Activity? = null

    init {
        myActivity = activity
    }

    fun post(reqType: Int, params: Map<String, String>) {
        //--- Do a http POST
        sendRequest(JSONObject(params), reqType, POST)
    }

    fun post(reqType: Int, postData: JSONObject) {
        //--- Do a http POST
        sendRequest(postData, reqType, POST)
    }

    operator fun get(reqType: Int, params: Map<String, String>) {
        //--- Do a http GET
        sendRequest(JSONObject(params), reqType, Request.Method.GET)
    }

    operator fun get(reqType: Int, postData: JSONObject) {
        //--- Do a http GET
        sendRequest(postData, reqType, Request.Method.GET)
    }

    private fun sendRequest(postData: JSONObject, action: Int, method: Int) {
        if (!AppUtil.isInternetAvailable(context)) {

            listener!!.onNoConnection()

            //---- Show dialog
            if (myActivity != null)
                showNoConnDialog()

            return
        }

        val url = AppUtil.getServerUrl(action)
        try {

            val timestamp = getTimeStamp()

            val finalData = JSONObject()
            finalData.put("tsp", timestamp)
            finalData.put("act", action.toString())
            finalData.put("ver", "1")
            finalData.put("data", postData)

            var userCode = 0
            val user = User.get()
            if (user != null) {
                userCode = user.userCode
            }
            finalData.put("ucode", "$userCode")

            Log.e("HTTP REQ: >>>", "$finalData at :$url")
            val jsonObjReq = object : JsonObjectRequest(method, url, finalData,
                Response.Listener { response ->
                    Log.e("RESP", response.toString())
                    try {
                        //------ Process response
                        response?.let { listener?.onSuccess(it) }
                    } catch (e: JSONException) {
                        val httpErr = HttpError(
                            10,
                            App.getAppInstance().applicationContext.getString(R.string.err_processing_http_response),
                            e
                        )
                        listener?.onError(httpErr)
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    VolleyLog.e("ERROR", "Error: " + error.message)

                    listener?.onError(getError(error))

                    val response = error.networkResponse
                    if (error is ServerError && response != null) {
                        try {
                            val parse = HttpHeaderParser.parseCharset(response.headers, "utf-8")
                            val res = String(
                                response.data,
                                Charset.forName(parse)
                            )
                            // Now you can use any deserializer to make sense of data
                            val obj = JSONObject(res)
                            Log.e("HTTP-ERROR", obj.toString())
                        } catch (e1: UnsupportedEncodingException) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace()
                        } catch (e2: JSONException) {
                            // returned data is not JSONObject?
                            e2.printStackTrace()
                        }
                    }
                }
            ) {
                override fun getHeaders(): Map<String, String> {
                    return getAuthHeaders(action, timestamp)
                }
            }

            jsonObjReq.retryPolicy = DefaultRetryPolicy(
                AppConfig.REQUEST_TIMEOUT,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            App.getAppInstance().addToRequestQueue(jsonObjReq)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setOnClientEventsListener(listener: ClientEventsListener) {
        this.listener = listener
    }

    private fun showNoConnDialog() {
        myActivity?.let { Alerts.showWarningMessage(it, R.string.dg_no_conn_text) }
    }

    private fun getTimeStamp(): String {
        val f = SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH)
        return f.format(Calendar.getInstance().time)
    }

    private fun getAuthHeaders(reqType: Int, timestamp: String): HashMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json; charset=utf-8"
        val authHeader = "1234";// AppUtil.getApiAuth(reqType, timestamp)
        headers["Authorization"] = authHeader

        return headers
    }

    private fun getError(error: VolleyError): HttpError {
        var errorNo = 0
        var message = context.getString(R.string.err_req_failed)

        when (error) {
            is TimeoutError, is NoConnectionError -> {
                errorNo = 1
                message = context.getString(R.string.err_req_timeout)
            }
            is AuthFailureError -> {
                errorNo = 2
                message = context.getString(R.string.err_auth_failed)
            }
            is ServerError -> errorNo = 3
            is NetworkError -> errorNo = 4
            is ParseError -> errorNo = 5
        }

        return HttpError(errorNo, message, error)
    }
}