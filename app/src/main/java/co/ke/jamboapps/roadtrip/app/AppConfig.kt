package co.ke.jamboapps.roadtrip.app

import co.ke.jamboapps.roadtrip.BuildConfig

class AppConfig {
    companion object {
        const val REQUEST_TIMEOUT: Int = 0
        const val IS_NETWORK_AVAILABLE: String = "isNetAvailable"
        const val LOCATION_UPDATE = "${BuildConfig.APPLICATION_ID}.loc_update"

        const val ACTION_NETWORK_STATE_CHANGE = "${BuildConfig.APPLICATION_ID}.network_state_change"
        const val ACTION_STOP_SERVICE = "${BuildConfig.APPLICATION_ID}.stop_loc_service"
        const val ACTION_END_ROUTE_MARKING = "${BuildConfig.APPLICATION_ID}.end_route_marking"
        const val ACTION_CANCEL_ROUTE_MARKING = "${BuildConfig.APPLICATION_ID}.cancel_route_marking"
        const val ACTION_SERVICE_STOPPED = "${BuildConfig.APPLICATION_ID}.service_stopped"
    }
}