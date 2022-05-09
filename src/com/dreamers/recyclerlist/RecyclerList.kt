package com.dreamers.recyclerlist

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dreamers.recyclerlist.utils.DynamicComponents
import com.dreamers.recyclerlist.utils.findViewByTag
import com.dreamers.recyclerlist.utils.getLayoutManager
import com.dreamers.recyclerlist.utils.getSnapHelper
import com.google.appinventor.components.annotations.SimpleEvent
import com.google.appinventor.components.annotations.SimpleFunction
import com.google.appinventor.components.annotations.SimpleProperty
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent
import com.google.appinventor.components.runtime.AndroidViewComponent
import com.google.appinventor.components.runtime.ComponentContainer
import com.google.appinventor.components.runtime.EventDispatcher
import com.google.appinventor.components.runtime.util.YailList
import kotlin.properties.Delegates

@Suppress("FunctionName")
class RecyclerList(private val container: ComponentContainer) : AndroidNonvisibleComponent(container.`$form`()) {

    private val context: Context = container.`$context`()
    private val dynamicComponents: DynamicComponents = DynamicComponents()

    private var recyclerView: RecyclerView? = null
    private var count: Int by Delegates.observable(0) { _, _, _ ->
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun createAdapter(): RecyclerView.Adapter<ViewHolder> {
        return AndroidViewAdapter(container, ::OnCreateView, ::OnBindView) { count }
    }

    @SimpleFunction(
        description = "Initialize recycler view inside a layout."
    )
    fun Initialize(
        `in`: AndroidViewComponent,
        layoutManager: String,
        snapHelper: String,
        orientation: Int,
        reverse: Boolean,
        spanCount: Int,
    ) {
        recyclerView = RecyclerView(context).apply {
            this.layoutManager = ListManager.valueOf(layoutManager).getLayoutManager(
                context,
                orientation,
                reverse,
                spanCount
            )
            adapter = createAdapter()
            ListSnapHelper.valueOf(snapHelper).getSnapHelper()?.attachToRecyclerView(this)
        }

        (`in`.view as ViewGroup).addView(
            recyclerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    @SimpleFunction(
        description = "Create a new component."
    )
    fun CreateComponent(`in`: AndroidViewComponent, name: Any, tag: String, properties: Any) {
        dynamicComponents.createComponent(`in`, name, tag, properties)
    }

    @SimpleFunction(
        description = "Create components using JSON template."
    )
    fun CreateTemplate(`in`: AndroidViewComponent, template: String, parameters: YailList) {
        dynamicComponents.createComponentsFromJson(`in`, template, parameters)
    }

    @SimpleFunction(
        description = "Set properties of a component. You can either use JSON string or dictionary to set properties."
    )
    fun SetProperties(view: AndroidViewComponent, properties: Any) {
        dynamicComponents.setProperties(view, properties)
    }

    @SimpleFunction(
        description = "Set unique id of a view"
    )
    fun SetUniqueId(view: AndroidViewComponent, id: String) {
        dynamicComponents.setUniqueId(view, id)
    }

    @SimpleFunction(
        description = "Get component with unique id."
    )
    fun GetUniqueId(view: AndroidViewComponent): String = dynamicComponents.getUniqueId(view)

    @SimpleFunction(
        description = "Get component using tag. Make sure to set RootParent before using."
    )
    fun GetComponent(root: AndroidViewComponent, tag: String): AndroidViewComponent? {
        val view = root.findViewByTag(tag)
        return dynamicComponents.getAndroidView(view)
    }

    @SimpleFunction(
        description = "Get root view using component."
    )
    fun GetRootView(view: AndroidViewComponent): AndroidViewComponent? {
        val viewHolder = recyclerView?.findContainingViewHolder(view.view)
        return if (viewHolder is ViewHolder) viewHolder.component else null
    }

    @SimpleFunction(
        description = "Returns true if the given component is dynamic."
    )
    fun IsDynamic(view: AndroidViewComponent): Boolean {
        return dynamicComponents.getAndroidView(view.view) != null
    }

    @SimpleFunction(
        description = "Scroll to position."
    )
    fun ScrollToPosition(position: Int) {
        recyclerView?.scrollToPosition(position.dec())
    }

    @SimpleFunction(
        description = "Smooth scroll to position."
    )
    fun SmoothScrollToPosition(position: Int) {
        recyclerView?.smoothScrollToPosition(position.dec())
    }

    @SimpleEvent(
        description = "Event raised to create UI. Don't bind any data to the UI."
    )
    fun OnCreateView(root: AndroidViewComponent) {
        EventDispatcher.dispatchEvent(this, "OnCreateView", root)
    }

    @SimpleEvent(
        description = "Event raised to bind data to UI."
    )
    fun OnBindView(root: AndroidViewComponent, position: Int) {
        EventDispatcher.dispatchEvent(this, "OnBindView", root, position)
    }

    @SimpleProperty(
        description = "Update recycler view item count. This causes recycler view to recreate views."
    )
    fun Count(count: Int) {
        this.count = count
    }

    @SimpleProperty(
        description = "Get recycler view item count."
    )
    fun Count() = count

    @SimpleProperty(
        description = "Returns the adapter position of the first visible view."
    )
    fun FirstVisibleItem(): Int {
        return when (val manager = recyclerView?.layoutManager) {
            is GridLayoutManager -> manager.findFirstVisibleItemPosition().inc()
            is StaggeredGridLayoutManager -> manager.findFirstVisibleItemPositions(IntArray(manager.spanCount)).first()
                .inc()
            is LinearLayoutManager -> manager.findFirstVisibleItemPosition().inc()
            else -> -1
        }
    }

    @SimpleProperty(
        description = "Returns the adapter position of the first fully visible view."
    )
    fun FirstCompletelyVisibleItem(): Int {
        return when (val manager = recyclerView?.layoutManager) {
            is GridLayoutManager -> manager.findFirstCompletelyVisibleItemPosition().inc()
            is StaggeredGridLayoutManager -> manager.findFirstCompletelyVisibleItemPositions(IntArray(manager.spanCount))
                .first().inc()
            is LinearLayoutManager -> manager.findFirstCompletelyVisibleItemPosition().inc()
            else -> -1
        }
    }

    @SimpleProperty(
        description = "Returns the adapter position of the last visible view."
    )
    fun LastVisibleItem(): Int {
        return when (val manager = recyclerView?.layoutManager) {
            is GridLayoutManager -> manager.findLastVisibleItemPosition().inc()
            is StaggeredGridLayoutManager -> manager.findLastVisibleItemPositions(IntArray(manager.spanCount)).last()
                .inc()
            is LinearLayoutManager -> manager.findLastVisibleItemPosition().inc()
            else -> -1
        }
    }

    @SimpleProperty(
        description = "Returns the adapter position of the last fully visible view."
    )
    fun LastCompletelyVisibleItem(): Int {
        return when (val manager = recyclerView?.layoutManager) {
            is GridLayoutManager -> manager.findLastCompletelyVisibleItemPosition().inc()
            is StaggeredGridLayoutManager -> manager.findLastCompletelyVisibleItemPositions(IntArray(manager.spanCount))
                .last()
                .inc()
            is LinearLayoutManager -> manager.findLastCompletelyVisibleItemPosition().inc()
            else -> -1
        }
    }

    @SimpleProperty
    fun LinearLayoutManager() = ListManager.Linear.name

    @SimpleProperty
    fun GridLayoutManager() = ListManager.Grid.name

    @SimpleProperty
    fun StaggeredLayoutManager() = ListManager.Staggered.name

    @SimpleProperty
    fun Vertical() = RecyclerView.VERTICAL

    @SimpleProperty
    fun Horizontal() = RecyclerView.HORIZONTAL

    @SimpleProperty
    fun LinearSnapHelper() = ListSnapHelper.Linear.name

    @SimpleProperty
    fun PagerSnapHelper() = ListSnapHelper.Pager.name

    @SimpleProperty
    fun NoSnapHelper() = ListSnapHelper.None.name
}
