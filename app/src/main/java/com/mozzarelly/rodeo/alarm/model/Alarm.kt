package com.mozzarelly.rodeo.alarm.model

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/*
{
    "on":false,
    "next":{"day":"today","enabled":false,"setting":"09:00"},
    "setting":"09:00",
    "hasTriggeredToday":false,
    "ringTimeToday":"09:00",
    "lastTriggered":{"day":"20190120","setting":"09:00","action":"disabled"},
    "times":["","07:30","07:30","07:30","07:30","07:30","10:00","","07:30","07:30","07:30","07:30","09:30","10:00"],
    "today":"monday_odd",
    "override":{"disabled":true,"days":2}
}
 */

data class Alarm(
    val on: Boolean,
    val next: AlarmSetting,
    val override: AlarmOverride?,
    val hasTriggeredToday: Boolean,
    val ringTimeToday: String?,
    val lastTriggered: AlarmSetting? = null,
    val settings: Array<Time?>,
    val today: Int
){
    fun days() = settings.indices.map {
        AlarmSetting(
            it,
            settings[it]
        )
    }
}

enum class DayNames {
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
}

data class AlarmSetting(
    val day: Int,
    val time: Time?
) {
    constructor(time: Time) : this(-1, time)
    fun enabled() = time != null
    fun weekNum() = if (day > 6) 2 else 1
    fun dayName() = DayNames.values()[day % 7].toString() + " " + weekNum()
}

data class AlarmOverride(
    val disable: Boolean?,
    val time: String?,
    val days: Int
)

data class Time(val hour: String, val minute: String) {
    companion object {
        val None = Time("", "")

        fun fromString(str: String?): Time  = when (str){
            null -> None
            "" -> None
            else -> {
                val (hour, min) = str.split(":")
                Time(hour, min)
            }
        }
    }

    override fun toString() = "${hour.toInt()}:${minute.padStart(2, '0')}"
}

class TimeAdapter {
    @FromJson fun fromString(str: String?): Time = Time.fromString(str)
    @ToJson fun toJson(time: Time): String = time.toString()
}
