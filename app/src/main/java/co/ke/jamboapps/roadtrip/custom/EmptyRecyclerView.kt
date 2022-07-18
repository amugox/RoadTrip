package co.ke.jamboapps.roadtrip.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Nullable
import androidx.recyclerview.widget.RecyclerView

class EmptyRecyclerView(context: Context, attrs: AttributeSet?): RecyclerView(context, attrs) {

    private var emptyView: View? = null

    private val emptyObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            val adapter = adapter
            if (adapter != null && emptyView != null) {
                if (adapter.itemCount == 0) {
                    emptyView!!.visibility = View.VISIBLE
                    this@EmptyRecyclerView.visibility = View.GONE
                } else {
                    emptyView!!.visibility = View.GONE
                    this@EmptyRecyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(emptyObserver)
        emptyObserver.onChanged()
    }

    fun setEmptyView(@Nullable emptyView: View) {
        this.emptyView = emptyView
    }
}