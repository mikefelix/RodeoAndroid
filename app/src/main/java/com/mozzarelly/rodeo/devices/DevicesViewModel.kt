package com.mozzarelly.rodeo.devices

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mozzarelly.rodeo.ListLCE
import com.mozzarelly.rodeo.devices.model.Device
import com.mozzarelly.rodeo.devices.model.DeviceGroup
import com.mozzarelly.rodeo.devices.model.DevicesContainer
import com.mozzarelly.rodeo.devices.model.Thermostat
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.lang.UnsupportedOperationException

class DevicesLCE : ListLCE<DevicesContainer, Device, DeviceRow>() {

    companion object {
        val api: DevicesApi = Retrofit.Builder()
            .baseUrl("https://mozzarelly.com/home/")
//            .baseUrl("https://holy-sun-2072.getsandbox.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(OkHttpClient.Builder().addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            ).build())
            .build()
            .create(DevicesApi::class.java)
    }

    private val devicesByName: MutableMap<String, Device?> = mutableMapOf()

    override fun DevicesContainer.asRows(): List<DeviceRow> {
        val rows = mutableListOf<DeviceRow>()

        for (group in this.groups) {
            rows += GroupLabel(group.name)
            for (i in (0 until (group.devices.size + 1) / 2)) {
                val first = group.devices[i * 2]
                val second = group.devices.getOrNull(i * 2 + 1)
                val row = DevicePair(get(first) ?: Device("unknown"), second?.let { get(it) })
                rows += row
            }
        }

        return rows
    }

    override fun DevicesContainer.getElementNames(): List<String> = groups.map { it.devices }.flatten()

    override fun DevicesContainer.getElement(name: String): Device? = devicesByName.computeIfAbsent(name, { Device(name) })

    override suspend fun doRefreshContainer(): DevicesContainer {
        val res = api.getDevices().await()
//        val res = DevicesContainer(listOf(DeviceGroup("Living room", listOf("aquarium", "fan")), DeviceGroup("Bedroom", listOf("bedheat", "bedlamp"))), listOf(), Thermostat())

        res.apply {
            for (dev in groups.map { it.devices }.flatten()){
                devicesByName[dev] = null
            }
        }

        return res
    }

    override suspend fun doUpdateElement(element: Device) = if (element.on)
        api.deviceOn(element.name).await()
    else
        api.deviceOff(element.name).await()

    override suspend fun doRefreshElement(name: String): Device {
        val o = api.getDevice(name).await()
        return o.copy(name = name)
    }

    override suspend fun doUpdate(container: DevicesContainer) {
        throw UnsupportedOperationException()
    }

    override fun setElement(name: String, element: Device?): Device? {
        return element?.copy(name = name).also {
            devicesByName[name] = it
        }
    }
}

class FakeDevicesLCE : ListLCE<DevicesContainer, Device, DeviceRow>() {
    val devices: MutableMap<String,Device?> = mutableMapOf(
        device("fan", null),
        device("bedheat", "bed"),
        device("bedlamp", "lamp"),
        device("outside"),
        device("garagedoor", "door"),
        device("garage", "lights"),
        device("floorlamp", "floor lamp"),
        device("couchlamp", "couch lamp"),
        device("aquarium"),
        device("scent"),
        device("wine")
    )

    val devicesResponse = DevicesContainer(listOf(
        DeviceGroup(
            "living room", listOf(
                "floorlamp", "couchlamp", "aquarium", "scent", "wine"
            )
        ),
        DeviceGroup(
            "bedroom", listOf(
                "fan", "bedheat", "bedlamp"
            )
        ),
        DeviceGroup(
            "garage", listOf(
                "garagedoor", "garage", "outside"
            )
        )
    ), listOf(), Thermostat())

    fun device(name: String, alias: String? = null) =
        name to Device(name, alias)

    override fun DevicesContainer.asRows(): List<DeviceRow> {
        val rows = mutableListOf<DeviceRow>()

        for (group in this.groups) {
            rows += GroupLabel(group.name)
            for (i in (0 until (group.devices.size + 1) / 2)) {
                val first = group.devices[i * 2]
                val second = group.devices.getOrNull(i * 2 + 1)
                val row = DevicePair(get(first)!!, second?.let { get(it) })
                rows += row
            }
        }

        return rows
    }

    override fun DevicesContainer.getElementNames(): List<String> = devices.keys.toList()

    override fun DevicesContainer.getElement(name: String): Device = devices[name]!!

    override suspend fun doRefreshContainer(): DevicesContainer {
        delay(2000)
        return devicesResponse
    }

    override suspend fun doUpdateElement(element: Device): Device {
        delay((Math.random() * 2500).toLong())
        devices[element.name] = element
        return getElement(element.name)
    }

    override suspend fun doRefreshElement(name: String): Device {
        delay((Math.random() * 2000).toLong())
        return devices[name]!!
    }

    override suspend fun doUpdate(container: DevicesContainer) {
        throw UnsupportedOperationException()
    }

    override fun setElement(name: String, element: Device?): Device? {
        devices[name] = element
        return element
    }
}
