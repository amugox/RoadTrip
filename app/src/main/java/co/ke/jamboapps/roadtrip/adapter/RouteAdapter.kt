package co.ke.jamboapps.roadtrip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.db.MyRoute
import co.ke.jamboapps.roadtrip.listener.ListItemClickListener
import java.text.SimpleDateFormat
import java.util.*

class RouteAdapter(
    private val data: MutableList<MyRoute>,
    private val listener: ListItemClickListener
) : RecyclerView.Adapter<RouteAdapter.ViewHolder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.route_list_item_view, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], position, listener, mContext!!)

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: MyRoute, pos: Int, listener: ListItemClickListener, ctx: Context) =
            with(itemView) {

                val f = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                findViewById<TextView>(R.id.tvName).text = item.routeName
                findViewById<TextView>(R.id.tvDesc).text = item.details
                if (item.createdOn != null)
                    findViewById<TextView>(R.id.tvDate).text = f.format(item.createdOn!!)

                if (item.myRoute == 1) {
                    findViewById<View>(R.id.ivMine).visibility = View.VISIBLE
                }

                setOnClickListener { v ->
                    listener.onItemClick(v, pos, false)
                }
            }
    }
}