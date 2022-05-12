package com.dreamers.recyclerlist

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val orientation: Int,
    private val spanCount: Int,
    private val gap: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            val position = parent.getChildAdapterPosition(view)
            if (orientation == RecyclerView.VERTICAL) {
                if (position < spanCount) top = gap
                if (position % spanCount == 0) left = gap
            } else {
                if (position < spanCount) left = gap
                if (position % spanCount == 0) top = gap
            }
            bottom = gap
            right = gap
        }
    }
}