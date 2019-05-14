@file:Suppress("unused")

package com.mozzarelly.rodeo

import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

@Suppress("unused")
object Utils {

    fun <T> MutableLiveData<T>.observe(owner: LifecycleOwner, lambda: (T) -> Unit) {
        this.observe(owner, Observer(lambda))
    }

    fun <T> LifecycleOwner.observe(data: MutableLiveData<T>, lambda: (T) -> Unit) {
        data.observe(this, Observer(lambda))
    }

    fun setUncaughtExceptionHandlerView(view: View) {
        Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
            override fun uncaughtException(thread: Thread, ex: Throwable) {
                Log.e("Rodeo", "Uncaught exception is: ", ex);
                ex.printStackTrace()
                Snackbar.make(view, "${ex.javaClass.name}: ${ex.message ?: "no message"}", Snackbar.LENGTH_LONG)
            }
        })
    }
}

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("android:visibility")
    fun View.setVisibility(value: Boolean) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("highlight")
    fun setHighlight(view: TextView, highlight: Boolean) {
        if (highlight) {
            view.setTypeface(null, Typeface.BOLD)
        } else {
            view.setTypeface(null, Typeface.NORMAL)
        }
    }
}