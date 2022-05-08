package com.dreamers.recyclerlist.utils

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.*
import com.dreamers.recyclerlist.ListManager
import com.dreamers.recyclerlist.ListSnapHelper
import com.google.appinventor.components.runtime.AndroidViewComponent


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

fun ListSnapHelper.getSnapHelper(): SnapHelper? {
    return when (this) {
        ListSnapHelper.Linear -> LinearSnapHelper()
        ListSnapHelper.Pager -> PagerSnapHelper()
        ListSnapHelper.None -> null
    }
}

fun AndroidViewComponent.findViewByTag(tag: String): View = view.findViewWithTag(tag)