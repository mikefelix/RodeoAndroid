package com.mozzarelly.rodeo.devices

import com.google.gson.annotations.SerializedName

/*

"thermostat": {
    "away": false,
    "temp": 68,
    "target": 68,
    "humidity": 35,
    "state": "off",
    "on": false,
    "mode": "heat"
  }

 */
data class Thermostat(
    @SerializedName("away") val away: Boolean,
    @SerializedName("on") val on: Boolean,
    @SerializedName("temp") val temp: Int,
    @SerializedName("target") val target: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("mode") val mode: String
){
    constructor(): this(false, false, 60, 60, 60, "eco")
}