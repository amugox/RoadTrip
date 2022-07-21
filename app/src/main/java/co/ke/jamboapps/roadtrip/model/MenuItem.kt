package co.ke.jamboapps.roadtrip.model

import com.mikepenz.iconics.typeface.IIcon

data class MenuItem(
    var code: Int = 0,
    var title: String = "",
    var details: String = "",
    var iconRes: Int = 0,
    var icon: IIcon? = null,
    var status: ItemStat? = null,
)
