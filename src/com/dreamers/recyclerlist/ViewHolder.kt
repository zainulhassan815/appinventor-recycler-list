package com.dreamers.recyclerlist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.appinventor.components.runtime.AndroidViewComponent
import com.google.appinventor.components.runtime.ComponentContainer
import com.google.appinventor.components.runtime.VerticalArrangement

class ViewHolder(val component: AndroidViewComponent) : RecyclerView.ViewHolder(component.view) {

    companion object {
        @JvmStatic
        fun create(container: ComponentContainer): ViewHolder {
            val root = VerticalArrangement(container).apply {
                // Remove root vertical arrangement from container
                (view.parent as ViewGroup).removeView(view)
            }
            return ViewHolder(root)
        }
    }

}
