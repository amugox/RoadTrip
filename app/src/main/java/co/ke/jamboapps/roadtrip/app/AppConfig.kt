package co.ke.jamboapps.roadtrip.app

class AppConfig {
    companion object {
        val REQUEST_TIMEOUT: Int = 0
        val PIN_LENGTH: Int = 5
        val IS_NETWORK_AVAILABLE: String = "isNetAvailable"

        val ACTION_NETWORK_STATE_CHANGE = "com.bmat.feetapclient.network_state_change"
//
//        val ACTION_FCM_REGISTER = "com.bmat.feetapclient.action.fcm_register"
//        val ACTION_FCM_UN_SUBSCRIBE = "com.bmat.feetapclient.action.fcm_un-subscribe"
//        val ACTION_TXN_PRINT = "com.bmat.feetapclient.action.txn_print"
//        val ACTION_TXN_NEW = "com.bmat.feetapclient.action.new_txn"
//
//        val ACTION_SYNC_DATA = "com.bmat.feetapclient.action.sync_data"
//        val ACTION_SYNC_COMPLETE = "com.bmat.feetapclient.action.sync_complete"
    }
}