package com.mozzarelly.rodeo

import android.os.Handler
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mozzarelly.rodeo.Utils.observe
import kotlinx.coroutines.*

abstract class LCE<X> : ViewModel(){
    val refreshing = MutableLiveData<Boolean>(false)

    val data: MutableLiveData<X?> = MutableLiveData()

    fun data(): X? = data.value

    fun observe(owner: LifecycleOwner, block: (x: X) -> Unit){
        owner.observe(data) {
            if (it != null) {
                block(it)
            }
        }
    }

    private val completeCallbacks = mutableSetOf<(X) -> Unit>()
    fun onLoadingComplete(block: (X) -> Unit){
        completeCallbacks.add(block)
    }

    private val beginLoadingCallbacks = mutableSetOf<() -> Unit>()
    fun onBeginLoading(block: () -> Unit){
        beginLoadingCallbacks.add(block)
    }

    private val errorCallbacks = mutableSetOf<(Throwable) -> Unit>()
    fun onError(block: (t: Throwable) -> Unit){
        errorCallbacks.add(block)
    }

    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun refresh() {
        refreshing.value = true
        beginLoadingCallbacks.callAll()

        try {
            viewModelScope.launch {
                val value = doRefresh()
                data.value = value
                refreshing.value = false
                afterRefresh()
                completeCallbacks.callAll(value)
            }
        }
        catch (e: Throwable) {
            Log.e("refresh", "Oops: Something went wrong. " + e.message)
            errorCallbacks.forEach { it(e) }
        }
    }

    fun update(value: X) {
        beginLoadingCallbacks.callAll()

        try {
            viewModelScope.launch {
                val newVal = doUpdate(value)
                completeCallbacks.callAll(newVal)
            }
        }
        catch (e: Throwable) {
            Log.e("updateDevice", "Oops: Something went wrong. " + e.message)
            errorCallbacks.forEach { it(e) }
        }
    }

    protected abstract suspend fun doRefresh(): X
    protected abstract suspend fun doUpdate(x: X): X

    open fun afterRefresh(){}

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

    override fun onCleared(){
        completeCallbacks.clear()
        beginLoadingCallbacks.clear()
        errorCallbacks.clear()
        viewModelScope.cancel()
    }
}