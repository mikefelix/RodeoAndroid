package com.mozzarelly.rodeo.devices

import com.mozzarelly.rodeo.devices.model.Device
import com.mozzarelly.rodeo.devices.model.DevicesContainer
import kotlinx.coroutines.Deferred
import retrofit2.http.*


interface DevicesApi {
    @GET("devicegroups")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun getDevices(): Deferred<DevicesContainer>

    @GET("device/{name}")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun getDevice(@Path("name") name: String): Deferred<Device>

    @PUT("device/{name}")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun deviceOn(@Path("name") name: String): Deferred<Device>

    @DELETE("device/{name}")
    @Headers("Authorization: Gd9kkwtTv7BW2p0Fg")
    fun deviceOff(@Path("name") name: String): Deferred<Device>
}