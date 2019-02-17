package com.mozzarelly.rodeo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;

data class Device(val name: String, val state: Boolean)

class DevicesViewModel : ViewModel() {
    private val devices: MutableLiveData<List<Device>> by lazy {
        MutableLiveData<List<Device>>().also { loadDevices() }
    }

    fun getDevices(): LiveData<List<Device>> {
        return devices
    }

    private fun loadDevices() {
        // Do an asynchronous operation to fetch devices.
    }
}
