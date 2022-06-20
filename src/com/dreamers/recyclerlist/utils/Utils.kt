package com.dreamers.recyclerlist.utils

import android.content.Context
import android.view.View
import android.view.animation.Interpolator
import androidx.recyclerview.widget.*
import com.dreamers.recyclerlist.ItemAnimator
import com.dreamers.recyclerlist.ListManager
import com.dreamers.recyclerlist.ListSnapHelper
import com.google.appinventor.components.runtime.AndroidViewComponent
import jp.wasabeef.recyclerview.animators.*


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

fun ItemAnimator.getAnimator(): RecyclerView.ItemAnimator {
    return when (this) {
        ItemAnimator.Default -> DefaultItemAnimator()
        ItemAnimator.LandingAnimator -> LandingAnimator()
        ItemAnimator.FadeInAnimator -> FadeInAnimator()
        ItemAnimator.FadeInDownAnimator -> FadeInDownAnimator()
        ItemAnimator.FadeInUpAnimator -> FadeInUpAnimator()
        ItemAnimator.FadeInLeftAnimator -> FadeInLeftAnimator()
        ItemAnimator.FadeInRightAnimator -> FadeInRightAnimator()
        ItemAnimator.ScaleInAnimator -> ScaleInAnimator()
        ItemAnimator.ScaleInTopAnimator -> ScaleInTopAnimator()
        ItemAnimator.ScaleInBottomAnimator -> ScaleInBottomAnimator()
        ItemAnimator.ScaleInLeftAnimator -> ScaleInLeftAnimator()
        ItemAnimator.ScaleInRightAnimator -> ScaleInRightAnimator()
        ItemAnimator.FlipInTopXAnimator -> FlipInTopXAnimator()
        ItemAnimator.FlipInBottomXAnimator -> FlipInBottomXAnimator()
        ItemAnimator.FlipInLeftYAnimator -> FlipInLeftYAnimator()
        ItemAnimator.FlipInRightYAnimator -> FlipInRightYAnimator()
        ItemAnimator.SlideInLeftAnimator -> SlideInLeftAnimator()
        ItemAnimator.SlideInRightAnimator -> SlideInRightAnimator()
        ItemAnimator.OvershootInLeftAnimator -> OvershootInLeftAnimator()
        ItemAnimator.OvershootInRightAnimator -> OvershootInRightAnimator()
        ItemAnimator.SlideInUpAnimator -> SlideInUpAnimator()
        ItemAnimator.SlideInDownAnimator -> SlideInDownAnimator()
    }
}