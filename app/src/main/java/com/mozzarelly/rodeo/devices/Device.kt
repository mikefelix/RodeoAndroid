package com.mozzarelly.rodeo.devices

import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("name") val name: String,
    @SerializedName("alias") val alias: String?,
    @SerializedName("on") var on: Boolean
){
    constructor(name: String) : this(name, null, false)

    fun desc() = (alias ?: name).capitalize()
}