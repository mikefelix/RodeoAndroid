package com.mozzarelly.rodeo.devices

import kotlinx.coroutines.Deferred
import okhttp3.Call
import retrofit2.http.*


interface DevicesApi {
    @GET("devicegroups")
    fun getDevices(): Deferred<Devices>

    @GET("devices/{name}")
    fun getDevice(@Path("name") name: String): Deferred<Device>

    @PUT("devices/{name}")
    fun updateDevice(@Path("name") name: String, @Body device: Device): Deferred<Device>
}