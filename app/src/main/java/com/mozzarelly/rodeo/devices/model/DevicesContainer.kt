package com.mozzarelly.rodeo.devices.model

import com.google.gson.annotations.SerializedName

data class DevicesContainer(
    @SerializedName("groups") val groups: List<DeviceGroup>,
    @SerializedName("ranges") val ranges: List<Range>,
    @SerializedName("thermostat") val thermostat: Thermostat
){
/*
    operator fun set(name: String, device: Device) {
        for (group in groups){
            val i = group.devicesState.indexOf(device)
            if (i >= 0){
                group.devicesState[i].on = device.on
            }
        }
    }
*/

    constructor(): this(emptyList(), emptyList(), Thermostat())

}