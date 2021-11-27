package co.ke.jamboapps.roadtrip.app

import android.app.Application
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class App: Application() {
    val TAG = App::class.java.simpleName
    private var mRequestQueue: RequestQueue? = null

    init {
        mInstance = this
//        appUser = null
    }

    companion object{
        private var mInstance: App? = null
        fun getAppInstance(): App {
            return mInstance as App
        }


    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    fun getRequestQueue(): RequestQueue? {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        }

        return mRequestQueue
    }


    fun <T> addToRequestQueue(req: Request<T>) {
        //req.setTag(TAG);
        getRequestQueue()?.add(req)
    }
}