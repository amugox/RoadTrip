package co.ke.jamboapps.roadtrip.model

import android.graphics.drawable.Drawable
import com.mikepenz.iconics.typeface.IIcon

data class ListItem (
    var code: String? = null,
    var name: String? = null,
    var icon: IIcon? = null,
    var iconDrawable: Drawable? = null,
    var value: String? = null
)
