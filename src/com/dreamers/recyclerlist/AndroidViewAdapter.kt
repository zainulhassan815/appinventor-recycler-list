package com.dreamers.recyclerlist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.appinventor.components.runtime.AndroidViewComponent
import com.google.appinventor.components.runtime.ComponentContainer
import com.google.appinventor.components.runtime.util.YailList

open class AndroidViewAdapter(
    private val container: ComponentContainer,
    private val onCreateView: (root: AndroidViewComponent) -> Unit,
    private val onBindView: (root: AndroidViewComponent, position: Int, dataItem: Any?) -> Unit,
    private var data: YailList = YailList.makeEmptyList(),
) : RecyclerView.Adapter<ViewHolder>() {

    fun updateData(data: YailList) {
        if (this.data != data) {
            this.data = data
        }
    }

    fun getData(): YailList = data

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ViewHolder {
        val viewHolder = ViewHolder.create(container)
        onCreateView(viewHolder.component)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        onBindView(viewHolder.component, position.inc(), data[position.inc()])
    }

    override fun getItemCount(): Int = data.size
}