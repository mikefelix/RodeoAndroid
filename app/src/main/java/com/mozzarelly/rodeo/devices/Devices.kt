package com.mozzarelly.rodeo.devices

import com.google.gson.annotations.SerializedName

data class Devices(
    @SerializedName("groups") val groups: List<DeviceGroup>,
    @SerializedName("ranges") val ranges: List<Range>,
    @SerializedName("thermostat") val thermostat: Thermostat
){
/*
    operator fun set(name: String, device: Device) {
        for (group in groups){
            val i = group.devices.indexOf(device)
            if (i >= 0){
                group.devices[i].on = device.on
            }
        }
    }
*/

    constructor(): this(emptyList(), emptyList(), Thermostat())
}