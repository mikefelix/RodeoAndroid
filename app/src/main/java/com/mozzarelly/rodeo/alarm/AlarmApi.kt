package com.mozzarelly.rodeo.alarm

import com.mozzarelly.rodeo.alarm.model.Alarm
import com.mozzarelly.rodeo.devices.model.Device
import com.mozzarelly.rodeo.devices.model.DevicesContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import retrofit2.http.*

/*
{
    "on":false,
    "next":{"day":"tomorrow","enabled":true,"time":"07:10"},
    "time":"07:10","hasTriggeredToday":true,
    "ringTimeToday":"07:10",
    "lastTriggered":{"day":"20190418","time":"07:10","action":"rung"},
    "times":["09:00","07:30","07:10","07:10","07:10","07:10","10:00"],
    "enabled":[false,true,true,true,true,true,true]
}
 */
interface AlarmApi {
    @GET("alarm")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun getAlarm(): Deferred<Alarm>

    @PUT("alarm")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun setAlarm(@Body alarm: Alarm): Deferred<Alarm>

    @POST("alarm/go")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun alarmOn(): Job

    @POST("alarm/stop")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun alarmOff(): Job
}