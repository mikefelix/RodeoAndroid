package com.mozzarelly.rodeo

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

interface HasName {
    val name: String
}

/**
 * C: Data container
 * E: Data element
 * R: View row
 */
abstract class ListLCE<C, E: HasName, R> : ViewModel(){
    var container: C? = null
    var rows: List<R> = listOf()

    operator fun get(name: String) = container?.getElement(name)
    operator fun set(name: String, e: E) = setElement(name, e)

    fun refresh(){
        if (rows.isEmpty()){
            refreshContainer()
        }
        else {
            refreshElements()
        }
    }

    fun refreshContainer(): Job {
        containerLoadingCallbacks.callAll()

        return GlobalScope.async(Dispatchers.IO) {
            try {
                val container = doRefreshContainer()
                this@ListLCE.container = container

                rows = container.asRows()

                for (name in container.getElementNames()) {
                    val e = container.getElement(name)
                    setElement(name, e)
//                    refreshElement(name)
                }

                afterRefresh(container)
                containerCompleteCallbacks.callAll(container)
            }
            catch (e: Throwable) {
                e.printStackTrace()
                Log.e("refreshContainer", "Oops: Something went wrong. " + e.message)
                containerErrorCallbacks.callAll(e)
            }
        }
    }

    fun refreshElements() {
        val container = container ?: return
        for (name in container.getElementNames()){
            refreshElement(name)
        }

        elementsCompleteCallbacks.callAll(container)
    }

    fun refreshElement(element: E): Job = refreshElement(element.name)

    fun refreshElement(name: String): Job {
        elementLoadingCallbacks[name].callAll()

        return GlobalScope.async(Dispatchers.IO) {
            try {
                val value = doRefreshElement(name)
                setElement(name, value)
                elementCompleteCallbacks[name].callAll(value)
            }
            catch (e: Throwable) {
                Log.e("refreshElement", "Oops: Something went wrong. " + e.message)
                elementErrorCallbacks.callAll(name, e)
            }
        }
    }

    fun updateContainer(c: C) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                doUpdate(c)
            }
            catch (e: Throwable) {
                Log.e("updateDevice", "Oops: Something went wrong. " + e.message)
            }
        }
    }

    fun updateElement(element: E) {
        elementLoadingCallbacks[element.name].callAll()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val res = doUpdateElement(element)
                val elem = setElement(element.name, res)
                elementCompleteCallbacks[element.name].callAll(elem)
            }
            catch (e: Throwable) {
                elementErrorCallbacks.callAll(element.name, e)
                Log.e("updateDevice", "Oops: Something went wrong. " + e.message)
            }
        }
    }

    fun getElement(name: String): E = container?.getElement(name) ?: throw RuntimeException("No element $name")

    protected abstract suspend fun doRefreshContainer(): C
    protected abstract suspend fun doRefreshElement(name: String): E
    protected abstract suspend fun doUpdate(container: C)
    protected abstract suspend fun doUpdateElement(element: E): E
    protected abstract fun setElement(name: String, element: E?): E?
    protected abstract fun C.asRows(): List<R>
    protected abstract fun C.getElement(name: String): E?
    protected abstract fun C.getElementNames(): List<String>

    protected open fun afterRefresh(c: C){}

    private val containerCompleteCallbacks = mutableSetOf<(C) -> Unit>()
    fun onLoadingContainerComplete(block: (C) -> Unit){
        containerCompleteCallbacks.add(block)
    }

    private val elementsCompleteCallbacks = mutableSetOf<(C) -> Unit>()
    fun onLoadingElementsComplete(block: (C) -> Unit){
        elementsCompleteCallbacks.add(block)
    }

    private val elementCompleteCallbacks = mutableMapOf<String, MutableSet<(E?) -> Unit>>()
    fun onLoadingElementComplete(name: String, block: (E?) -> Unit){
        elementCompleteCallbacks.computeIfAbsent(name) { mutableSetOf() }.add(block)
    }

    private val containerLoadingCallbacks = mutableSetOf<() -> Unit>()
    fun onBeginLoadingContainer(block: () -> Unit){
        containerLoadingCallbacks.add(block)
    }

    private val elementLoadingCallbacks = mutableMapOf<String, MutableSet<() -> Unit>>()
    fun onBeginLoadingElement(name: String, block: () -> Unit){
        elementLoadingCallbacks.computeIfAbsent(name) { mutableSetOf() }.add(block)
    }

    private val containerErrorCallbacks = mutableSetOf<(Throwable) -> Unit>()
    fun onErrorLoadingContainer(block: (t: Throwable) -> Unit){
        containerErrorCallbacks.add(block)
    }

    private val elementErrorCallbacks = mutableSetOf<(String, Throwable) -> Unit>()
    fun onErrorLoadingElement(block: (name: String, t: Throwable) -> Unit){
        elementErrorCallbacks.add(block)
    }

    private fun <A: Function0<*>> Set<A>?.callAll(){
        this?.forEach { handler.post(Runnable {it.invoke()}) }
    }

    private fun <A, F: Function1<A, *>> Set<F>?.callAll(a: A){
        this?.forEach { handler.post(Runnable {it.invoke(a)}) }
    }

    private fun <A, B, F: Function2<A, B, *>> Set<F>?.callAll(a: A, b: B){
        this?.forEach { handler.post(Runnable {it.invoke(a, b)}) }
    }

    private val handler = Handler()
}