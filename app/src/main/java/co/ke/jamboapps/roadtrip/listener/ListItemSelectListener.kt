package co.ke.jamboapps.roadtrip.listener

import co.ke.jamboapps.roadtrip.model.ListItem


interface ListItemSelectListener {
    fun onItemSelected(item: ListItem, position: Int)
}