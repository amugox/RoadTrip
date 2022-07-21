package co.ke.jamboapps.roadtrip.util

import android.content.Context
import android.location.Location
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.clazz.DirectionsJSONParser
import co.ke.jamboapps.roadtrip.clazz.HttpClient
import co.ke.jamboapps.roadtrip.clazz.HttpReqActions
import co.ke.jamboapps.roadtrip.db.MyRoute
import co.ke.jamboapps.roadtrip.db.RouteMark
import co.ke.jamboapps.roadtrip.model.RouteAssistItem
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class RouteUtil {
    companion object {
        fun createMark(routeCode: Int, type: Int, location: Location?) {
            location?.let {
                val mark = RouteMark(
                    code = routeCode,
                    lat = it.latitude,
                    lon = it.longitude,
                    type = type
                )
                mark.save()
            }
        }

        fun uploadMarks(context: Context, routeCode: Int) {
            val marks = RouteMark.getByRoute(routeCode)
            val arrayObj = JSONArray()
            marks.forEach { m ->
                arrayObj.put(JSONObject().apply {
                    put("typ", m.type)
                    put("lat", m.lat)
                    put("lon", m.lon)
                    put("txt", m.text)
                })
            }

            val httpClient = HttpClient()
            httpClient.setOnClientEventsListener(object : HttpClient.ClientEventsListener {
                override fun onSuccess(response: JSONObject) {
                    try {
                        val stat = response.getInt("stat")
                        if (stat == 0) {
                            val route = MyRoute.get(routeCode)
                            route?.let {
                                it.markStat = 3
                                it.update()
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

            val data = JSONObject().apply {
                put("mks", arrayObj)
                put("rt_code", routeCode)
            }
            httpClient.post(HttpReqActions.ROUTE_CREATE_MARKS, data)
        }

        fun getAssistData(): MutableList<RouteAssistItem> {
//            1 Turn left
//            2 Turn right
//            3 Make a U-Turn
//            4 Make a U-Turn on left
//            5 Make a U-Turn on right
//            6 Take First exit
//            7 Take Second exit
//            8 Take Third exit
//            9 Take Fourth exit
//            10 Keep right
//            11 Keep left
//            12 Bump
//            13 Pothole
//            14 Sharp corner
//            15 Narrow road
            val data = mutableListOf<RouteAssistItem>()

            data.add(
                RouteAssistItem(
                    code = 1,
                    title = "Turn Right",
                    details = "Turn right",
                    iconRes = R.drawable.ic_turn_right
                )
            )
            data.add(
                RouteAssistItem(
                    code = 2,
                    title = "Turn Left",
                    details = "Turn left",
                    iconRes = R.drawable.ic_turn_left
                )
            )
//            data.add(
//                RouteAssistItem(
//                    code = 3,
//                    title = "U-Turn",
//                    details = "Make a U-Turn",
//                    iconRes = R.drawable.ic_u_turn_right
//                )
//            )
            data.add(
                RouteAssistItem(
                    code = 4,
                    title = "U-Turn on Left",
                    details = "Make a U-Turn on left",
                    iconRes = R.drawable.ic_u_turn_left
                )
            )
            data.add(
                RouteAssistItem(
                    code = 5,
                    title = "U-Turn on Right",
                    details = "Make a U-Turn on right",
                    iconRes = R.drawable.ic_u_turn_right
                )
            )
            data.add(
                RouteAssistItem(
                    code = 6,
                    title = "First Exit",
                    details = "Take First exit",
                    iconRes = R.drawable.ic_exit_1
                )
            )
            data.add(
                RouteAssistItem(
                    code = 7,
                    title = "Second Exit",
                    details = "Take Second exit",
                    iconRes = R.drawable.ic_exit_2
                )
            )
            data.add(
                RouteAssistItem(
                    code = 8,
                    title = "Third Exit",
                    details = "Take Third exit",
                    iconRes = R.drawable.ic_exit_3
                )
            )
//            data.add(
//                RouteAssistItem(
//                    code = 9,
//                    title = "Fourth exit",
//                    details = "Take Fourth exit",
//                    icon = FontAwesome.Icon.faw_map_pin
//                )
//            )
            data.add(
                RouteAssistItem(
                    code = 10,
                    title = "Keep Right",
                    details = "Keep right",
                    iconRes = R.drawable.ic_keep_right
                )
            )
            data.add(
                RouteAssistItem(
                    code = 11,
                    title = "Keep Left",
                    details = "Keep left",
                    iconRes = R.drawable.ic_keep_left
                )
            )
            data.add(
                RouteAssistItem(
                    code = 12,
                    title = "Road Bump",
                    details = "Bump ahead",
                    iconRes = R.drawable.ic_car_bump
                )
            )
            data.add(
                RouteAssistItem(
                    code = 13,
                    title = "Pothole",
                    details = "Pothole ahead",
                    iconRes = R.drawable.ic_pothole
                )
            )
            data.add(
                RouteAssistItem(
                    code = 14,
                    title = "Sharp Corner",
                    details = "Sharp corner ahead",
                    iconRes = R.drawable.ic_sharp_corner
                )
            )
            data.add(
                RouteAssistItem(
                    code = 15,
                    title = "Narrow Road",
                    details = "Narrow road ahead",
                    iconRes = R.drawable.ic_narrow_road
                )
            )
            data.add(
                RouteAssistItem(
                    code = 16,
                    title = "Slow Down",
                    details = "Slow down",
                    iconRes = R.drawable.ic_slow_down
                )
            )
            return data
        }

        fun getRouteDirections(
            context: Context,
            routeCode: Int
        ): List<List<HashMap<String, String>>> {
            val start = RouteMark.getStartMark(routeCode)
            val endMark = RouteMark.getEndMark(routeCode)
            if (start == null || endMark == null)
                return listOf()

//            val marks = RouteMark.getWaypoints(routeCode)
//            if (marks.size > 0) {
//
//            }

            val apiKey = "key=" + context.getString(R.string.google_maps_key)
            val origin = "origin=${start.lat},${start.lon}"
            val dest = "destination=${endMark.lat},${endMark.lon}"
            val extras = "sensor=false&mode=driving"
            val wPoints = "waypoints="
            val parameters = "$apiKey&$origin&$dest&$extras"
            val strUrl = "https://maps.googleapis.com/maps/api/directions/json?$parameters"

            var data = ""
            var iStream: InputStream? = null
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(strUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connect()

                iStream = urlConnection.inputStream
                val br = BufferedReader(InputStreamReader(iStream))
                val sb = StringBuffer()

                var line: String? = ""
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }

                data = sb.toString()
                br.close()

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                iStream?.close()
                urlConnection?.disconnect()
            }

            val jObject = JSONObject(data)
            val parser = DirectionsJSONParser()
            return parser.parse(jObject)
        }
    }
}