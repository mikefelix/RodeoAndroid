package com.mozzarelly.rodeo

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

@Suppress("unused")
object Utils {

    fun <T> MutableLiveData<T>.observe(owner: LifecycleOwner, lambda: (T) -> Unit) {
        this.observe(owner, Observer(lambda))
    }

    fun <T> LifecycleOwner.observe(data: MutableLiveData<T>, lambda: (T) -> Unit) {
        data.observe(this, Observer(lambda))
    }

}