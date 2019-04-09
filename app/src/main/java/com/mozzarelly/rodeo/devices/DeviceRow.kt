package com.mozzarelly.rodeo.devices

import androidx.lifecycle.MutableLiveData
import com.mozzarelly.rodeo.devices.model.Device

typealias DeviceData = MutableLiveData<Device>

interface DeviceRow {
    val viewType: Int
}

data class GroupLabel(val name: String) : DeviceRow {
    companion object {
        const val viewType = 1
    }

    override val viewType = GroupLabel.viewType
}

data class DevicePair(val first: Device, val second: Device?) : DeviceRow {
    companion object {
        const val viewType = 2
    }

    override val viewType = DevicePair.viewType

    override fun toString() = "first: ${first.describe()}; second: ${second.describe()}"
}

fun Device?.describe(): String = this?.name ?: "none"

fun DeviceData?.describe(): String {
    this ?: return "none"
    val value = this.value ?: "empty"
    val hasObserver = if (this.hasObservers()) " (!)" else ""
    return "$value$hasObserver"
}