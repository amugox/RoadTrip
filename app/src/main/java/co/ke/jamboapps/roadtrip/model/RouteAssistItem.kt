package co.ke.jamboapps.roadtrip.model

import com.mikepenz.iconics.typeface.IIcon

data class RouteAssistItem(
    var code: Int = 0,
    var title: String = "",
    var details: String = "",
    var icon: IIcon? = null,
    var iconRes: Int = 0
)
