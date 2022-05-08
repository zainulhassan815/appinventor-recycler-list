package com.dreamers.recyclerlist.utils

import android.view.View
import com.google.appinventor.components.runtime.AndroidViewComponent
import com.google.appinventor.components.runtime.Component
import com.google.appinventor.components.runtime.ComponentContainer
import com.google.appinventor.components.runtime.errors.YailRuntimeError
import com.google.appinventor.components.runtime.util.YailList
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.reflect.Method

class DynamicComponents {

    companion object {
        const val LOG_TAG = "DynamicComponents"
    }

    private val androidViews: MutableMap<View, AndroidViewComponent> = mutableMapOf()
    private val uniqueIds: MutableMap<AndroidViewComponent, String> = mutableMapOf()

    fun getAndroidView(view: View): AndroidViewComponent? = androidViews[view]

    fun setUniqueId(view: AndroidViewComponent, id: String) {
        uniqueIds[view] = id
    }

    fun getUniqueId(view: AndroidViewComponent): String = uniqueIds[view]!!

    private fun getClassName(componentName: Any): String {
        val regex = "[^.$@a-zA-Z0-9]"
        val componentNameString = componentName.toString().replace(regex.toRegex(), "")
        return if (componentName is String && componentNameString.contains(".")) {
            componentNameString
        } else if (componentName is String) {
            val base = "com.google.appinventor.components.runtime."
            base + componentNameString
        } else if (componentName is Component) {
            componentName.javaClass.name.replace(regex.toRegex(), "")
        } else throw YailRuntimeError("Component is invalid.", "")
    }


    private fun invoke(component: Component?, name: String, parameters: Array<Any>) {
        if (component != null) {
            val mMethods = component.javaClass.methods
            try {
                // Get method to call with given parameters
                val mMethod: Method = getMethod(mMethods, name, parameters.size)!!
                val mRequestedMethodParameters: Array<Class<*>> = mMethod.parameterTypes

                val mParametersArrayList = ArrayList<Any>()

                for (i in mRequestedMethodParameters.indices) {
                    val param = parameters[i].toString()
                    when (mRequestedMethodParameters[i].name) {
                        "int" -> mParametersArrayList.add(param.toInt())
                        "float" -> mParametersArrayList.add(param.toFloat())
                        "double" -> mParametersArrayList.add(param.toDouble())
                        "java.lang.String" -> mParametersArrayList.add(param)
                        "boolean" -> mParametersArrayList.add(param.toBoolean())
                        else -> mParametersArrayList.add(parameters[i])
                    }
                }
                mMethod.invoke(component, *mParametersArrayList.toArray())
            } catch (e: Exception) {
                throw YailRuntimeError(e.message, LOG_TAG)
            }
        } else throw YailRuntimeError("Component cannot be null.", LOG_TAG)
    }

    private fun getMethod(methods: Array<Method>, name: String, parameterCount: Int): Method? {
        val formattedName = name.replace("[^a-zA-Z0-9]".toRegex(), "")
        for (method in methods) {
            val methodParameterCount = method.parameterTypes.size
            if (method.name == formattedName && methodParameterCount == parameterCount) {
                return method
            }
        }
        return null
    }

    private fun parse(id: String, json: JSONObject, propertiesArray: JSONArray) {
        try {
            val data = JSONObject(json.toString())
            data.remove("components")
            if ("" != id) {
                data.put("in", id)
            }
            propertiesArray.put(data)

            if (json.has("components")) {
                for (i in 0 until json.getJSONArray("components").length()) {
                    parse(data.optString("id", ""), json.getJSONArray("components").getJSONObject(i), propertiesArray)
                }
            }
        } catch (e: JSONException) {
            throw YailRuntimeError("Error while parsing json template", LOG_TAG)
        }
    }

    private fun isNotEmptyOrNull(item: Any?): Boolean {
        if (item is String) {
            var mItem = item.toString()
            mItem = mItem.replace(" ", "")
            return mItem.isNotEmpty()
        }
        return item != null
    }

    fun setProperties(component: AndroidViewComponent, properties: Any) {
        val propertiesString = if (properties is String) properties else properties.toString()
        try {
            if (propertiesString.isEmpty()) return
            val mProperties = JSONObject(propertiesString)
            val mPropertyNames = mProperties.names()
            for (i in 0 until mProperties.length()) {
                val name = mPropertyNames!!.getString(i)
                val value = mProperties[name]
                invoke(component, name, arrayOf(value))
            }
        } catch (e: JSONException) {
            throw YailRuntimeError(e.message, LOG_TAG)
        }
    }

    fun createComponent(
        parent: AndroidViewComponent,
        name: Any,
        tag: String,
        properties: Any,
    ): AndroidViewComponent {
        val clazzName = getClassName(name)
        val clazz = Class.forName(clazzName)
        val constructor = clazz.getConstructor(ComponentContainer::class.java)
        val component = constructor.newInstance(parent) as AndroidViewComponent
        // assign tag to the component
        component.view.tag = tag
        // Set properties
        setProperties(component, properties)
        androidViews[component.view] = component
        return component
    }

    fun createComponentsFromJson(
        `in`: AndroidViewComponent,
        template: String,
        parameters: YailList,
    ) {
        try {
            var mScheme = JSONObject(template)
            var newTemplate = template

            if (isNotEmptyOrNull(template) && mScheme.has("components")) {

                val propertiesArray = JSONArray()

                val mKeys = if (mScheme.has("keys")) mScheme.getJSONArray("keys") else JSONArray()

                if (isNotEmptyOrNull(mKeys) && mKeys.length() == parameters.length() - 1) {
                    for (i in 0 until mKeys.length()) {
                        val keyPercent = "%" + mKeys.getString(i)
                        val keyBracket = "{" + mKeys.getString(i) + "}"
                        val value = parameters.getString(i).replace("\"", "")
                        newTemplate = newTemplate.replace(keyPercent, value)
                        newTemplate = newTemplate.replace(keyBracket, value)
                    }
                }
                mScheme = JSONObject(newTemplate)
                parse("", mScheme, propertiesArray)
                propertiesArray.remove(0)
                for (i in 0 until propertiesArray.length()) {
                    if (!propertiesArray.getJSONObject(i).has("id")) {
                        throw YailRuntimeError(
                            "One or multiple components do not have a specified ID in the template.",
                            LOG_TAG
                        )
                    }
                    val mJson = propertiesArray.getJSONObject(i)
                    val mId = mJson.getString("id")
                    val mRoot = if (!mJson.has("in")) `in` else {
                        androidViews[`in`.findViewByTag(mJson.getString("in"))]!!
                    }
                    val mType = mJson.getString("type")

                    val properties = if (mJson.has("properties")) mJson.getJSONObject("properties").toString() else ""
                    createComponent(mRoot, mType, mId, properties)
                }
            } else {
                throw YailRuntimeError("The template is empty, or is does not have any components.", LOG_TAG)
            }
        } catch (jsonException: JSONException) {
            throw YailRuntimeError("Failed to create schema.", LOG_TAG)
        }
    }

}