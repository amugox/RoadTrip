package co.ke.jamboapps.roadtrip.listener

import android.view.View

interface ListItemClickListener {
    fun onItemClick(v: View, position: Int, isLong: Boolean)
}