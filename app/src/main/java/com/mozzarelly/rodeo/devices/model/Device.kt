package com.mozzarelly.rodeo.devices.model

import com.google.gson.annotations.SerializedName
import com.mozzarelly.rodeo.HasName

data class Device(
    @SerializedName("name") override val name: String,
    @SerializedName("alias") val alias: String?,
    @SerializedName("on") var on: Boolean
): HasName {
    constructor(name: String) : this(name, null, false)

    fun desc() = (alias ?: name).capitalize()
}