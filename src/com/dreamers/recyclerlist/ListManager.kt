package com.dreamers.recyclerlist

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

enum class ListManager {
    Linear,
    Grid,
    Staggered
}

fun ListManager.getLayoutManager(
    context: Context,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
    reverse: Boolean = false,
    spanCount: Int = 1,
): RecyclerView.LayoutManager {
    return when (this) {
        ListManager.Linear -> LinearLayoutManager(context, orientation, reverse)
        ListManager.Grid -> GridLayoutManager(context, spanCount, orientation, reverse)
        ListManager.Staggered -> StaggeredGridLayoutManager(spanCount, orientation)
    }
}