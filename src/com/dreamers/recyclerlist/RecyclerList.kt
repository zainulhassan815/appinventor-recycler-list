package com.dreamers.recyclerlist

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dreamers.recyclerlist.utils.FunctionInvoker
import com.google.appinventor.components.annotations.SimpleFunction
import com.google.appinventor.components.annotations.SimpleProperty
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent
import com.google.appinventor.components.runtime.AndroidViewComponent
import com.google.appinventor.components.runtime.ComponentContainer
import com.google.appinventor.components.runtime.VerticalArrangement
import gnu.mapping.ProcedureN
import kotlin.properties.Delegates

@Suppress("FunctionName")
class RecyclerList(private val container: ComponentContainer) : AndroidNonvisibleComponent(container.`$form`()) {

    private val context: Context = container.`$context`()
    private val invoker: FunctionInvoker = FunctionInvoker(form)

    private var recyclerView: RecyclerView? = null
    private var count by Delegates.observable(0) { _, _, _ ->
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    private fun String.findProcedure(): ProcedureN = invoker.lookupProcedure(this)

    @Suppress("UNCHECKED_CAST")
    private fun <T> ProcedureN.call(vararg args: Any?): T {
        return invoker.call(this, args.toList()) as T
    }

    private fun createAdapter(
        container: ComponentContainer,
        createView: ProcedureN,
        bindView: ProcedureN,
        getCount: () -> Int
    ): RecyclerView.Adapter<ViewHolder> {
        return object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): ViewHolder {
                return ViewHolder.create(container) {
                    createView.call(it)
                }
            }

            override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
                bindView.call<Unit>(viewHolder.component, position)
            }

            override fun getItemCount(): Int = getCount()
        }
    }

    @SimpleFunction(
        description = "Initialize recycler view inside a layout."
    )
    fun Initialize(
        `in`: AndroidViewComponent,
        layoutManager: String,
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
        description = "Setup recycler view adapter. You need to pass name of procedures to create view and bind data to the view."
    )
    fun SetupAdapter(createView: String, bindView: String) {
        val createViewProcedure = createView.findProcedure()
        val bindViewProcedure = bindView.findProcedure()
        val adapter = createAdapter(container, createViewProcedure, bindViewProcedure) { count }
        recyclerView?.adapter = adapter
    }

    @SimpleProperty
    fun LinearLayoutManager() = ListManager.Linear.name

    @SimpleProperty
    fun GridLayoutManager() = ListManager.Grid.name

    @SimpleProperty
    fun StaggeredLayoutManager() = ListManager.Staggered.name

}

class ViewHolder(val component: AndroidViewComponent) : RecyclerView.ViewHolder(component.view) {

    companion object {
        @JvmStatic
        fun create(
            container: ComponentContainer,
            children: (container: ComponentContainer) -> AndroidViewComponent
        ): ViewHolder {
            val root = VerticalArrangement(container).apply {
                `$add`(children(container))
            }
            return ViewHolder(root)
        }
    }

}
