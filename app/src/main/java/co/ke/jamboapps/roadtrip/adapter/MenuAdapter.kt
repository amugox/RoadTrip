package co.ke.jamboapps.roadtrip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.listener.ListItemClickListener
import co.ke.jamboapps.roadtrip.model.MenuItem
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.utils.sizeDp

class MenuAdapter(
    private val data: MutableList<MenuItem>,
    private val listener: ListItemClickListener
) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    private var mContext: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context

        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item_view, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(data[position], position, listener, mContext!!)

    override fun getItemCount() = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: MenuItem, pos: Int, listener: ListItemClickListener, ctx: Context) =
            with(itemView) {
                findViewById<TextView>(R.id.tvTitle).text = item.title

                if (item.details.isNotEmpty()) {
                    findViewById<TextView>(R.id.tvDets).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.tvDets).text = item.details
                }

                if (item.iconRes != 0) {
                    findViewById<ImageView>(R.id.icon).setBackgroundResource(item.iconRes)
                }
                if (item.icon != null) {
                    val myIcon = IconicsDrawable(ctx, item.icon!!).apply {
                        sizeDp = 40
                    }
                    findViewById<ImageView>(R.id.icon).setImageDrawable(myIcon)
                }

                setOnClickListener { v ->
                    listener.onItemClick(v, pos, false)
                }
            }
    }
}