package com.dreamers.recyclerlist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.appinventor.components.runtime.AndroidViewComponent
import com.google.appinventor.components.runtime.ComponentContainer

open class AndroidViewAdapter(
    private val container: ComponentContainer,
    private val onCreateView: (root: AndroidViewComponent) -> Unit,
    private val onBindView: (root: AndroidViewComponent, position: Int) -> Unit,
    private val getCount: () -> Int,
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ViewHolder {
        val viewHolder = ViewHolder.create(container)
        onCreateView(viewHolder.component)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        onBindView(viewHolder.component, position.inc())
    }

    override fun getItemCount(): Int = getCount()
}