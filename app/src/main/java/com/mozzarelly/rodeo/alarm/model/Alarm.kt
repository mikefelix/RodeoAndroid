package com.mozzarelly.rodeo.alarm.model

import com.mozzarelly.rodeo.alarm.AlarmViewModel

/*
{
    "on":false,
    "next":{"day":"today","enabled":false,"time":"09:00"},
    "time":"09:00",
    "hasTriggeredToday":false,
    "ringTimeToday":"09:00",
    "lastTriggered":{"day":"20190120","time":"09:00","action":"disabled"},
    "times":["09:00","07:30","07:30","07:30","07:30","07:30","10:00"],
    "enabled":[false,true,true,true,true,true,true]
}

{
    "on":false,
    "next":{"day":"today","enabled":false,"time":"09:00"},
    "time":"09:00",
    "hasTriggeredToday":false,
    "ringTimeToday":"09:00",
    "lastTriggered":{"day":"20190120","time":"09:00","action":"disabled"},
    "times":["","07:30","07:30","07:30","07:30","07:30","10:00","","07:30","07:30","07:30","07:30","09:30","10:00"],
    "today":"monday_odd",
    "override":{"disabled":true,"days":2}
}
 */

data class Alarm(
    val on: Boolean,
    val next: AlarmTime,
    val time: String,
    val override: AlarmOverride?,
    val hasTriggeredToday: Boolean,
    val ringTimeToday: String?,
    val lastTriggered: AlarmTime? = null,
    val times: Array<String?>,
    val today: String
){
    fun days() = times.indices.map {
        AlarmTime(
            DayNames.values()[it % 7].toString(),
            it < 7,
            times[it],
            !times[it].isNullOrEmpty()
        )
    }

}

enum class DayNames {
    Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
}

data class AlarmTime(
    val day: String,
    val oddWeek: Boolean,
    val time: String?,
    val enabled: Boolean
)

data class AlarmOverride(
    val disable: Boolean?,
    val time: String?,
    val days: Int
)