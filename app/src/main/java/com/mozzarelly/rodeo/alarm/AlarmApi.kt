package com.mozzarelly.rodeo.alarm

import com.mozzarelly.rodeo.alarm.model.Alarm
import com.mozzarelly.rodeo.alarm.model.Time
import com.mozzarelly.rodeo.devices.model.Device
import com.mozzarelly.rodeo.devices.model.DevicesContainer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import retrofit2.http.*

/*
{
    "on":false,
    "next":{"day":"tomorrow","enabled":true,"setting":"07:10"},
    "setting":"07:10","hasTriggeredToday":true,
    "ringTimeToday":"07:10",
    "lastTriggered":{"day":"20190418","setting":"07:10","action":"rung"},
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

    @POST("alarm/{days}/{set}")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun saveSetting(@Path("days") days: String, @Path("set") set: String): Deferred<Unit>

    @POST("alarm/t1/{set}")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun saveOverride(@Path("set") set: String): Deferred<Unit>

    @POST("alarm/t{days}/off")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun disable(@Path("days") days: Int): Deferred<Unit>

    @POST("alarm/t{days}/on")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun undisable(@Path("days") days: String): Deferred<Unit>

}