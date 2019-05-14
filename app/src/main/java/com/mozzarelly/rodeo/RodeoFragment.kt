package com.mozzarelly.rodeo

import android.view.View

abstract class RodeoFragment : androidx.fragment.app.Fragment() {
    var rootView: View? = null
        set(value){
            field = value
            value?.let { Utils.setUncaughtExceptionHandlerView(it) }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        rootView = null
    }

    override fun onResume() {
        super.onResume()
        rootView?.let { Utils.setUncaughtExceptionHandlerView(it) }
    }
}