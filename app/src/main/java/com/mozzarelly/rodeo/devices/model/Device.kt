package com.mozzarelly.rodeo.devices.model

import com.google.gson.annotations.SerializedName
import com.mozzarelly.rodeo.HasName

data class Device(
    @SerializedName("name") override val name: String,
    @SerializedName("alias") val alias: String?,
    @SerializedName("on") var on: Boolean,
    @SerializedName("offline") var offline: Boolean,
    @SerializedName("overridden") var overridden: Boolean
): HasName {
    constructor(name: String) : this(name, null, false, false, false)
    constructor(name: String, alias: String?) : this(name, alias, false, false,false)

    fun desc() = (alias ?: name).capitalize()
}