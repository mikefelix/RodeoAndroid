package com.mozzarelly.rodeo.devices

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class DevicesViewModel : ViewModel() {
    val devices: MutableLiveData<Devices?> = MutableLiveData()

    val devicesByName: MutableMap<String, MutableLiveData<Device>> = mutableMapOf()

    val api = Retrofit.Builder()
        .baseUrl("https://holy-sun-2072.getsandbox.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        ).build())
        .build()
        .create(DevicesApi::class.java)

    fun loadDevicesAsync(): Job {
        return GlobalScope.async(Dispatchers.IO){
            try {
                val req = api.getDevices(/*"Gd9kkwtTv7BW2p0Fg"*/)
                req.await().also { response ->
                    for (group in response.groups){
                        for (device in group.devices){
                            if (!devicesByName.containsKey(device))
                                devicesByName[device] = MutableLiveData()

                            getDeviceAsync(device)
                        }
                    }

                    devices.postValue(response)
                }
            }
            catch (e: Throwable) {
                Log.e("loadDevices", "Oops: Something went wrong. " + e.message)
                throw e
            }
        }
    }

    fun getDeviceAsync(name: String): Job {
        return GlobalScope.launch(Dispatchers.IO) {
            try {
                val req = api.getDevice(name)
                val dev = req.await()
                devicesByName[name]!!.postValue(dev)
            }
            catch (e: Throwable){
                Log.e("getDevice", "Oops: Something went wrong. " + e.message)
            }
        }
    }

    fun updateDevice(device: Device){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val req = api.updateDevice(device.name, device)
                val dev = req.await()
                devicesByName[device.name]?.postValue(dev)
            }
            catch (e: Throwable){
                Log.e("updateDevice", "Oops: Something went wrong. " + e.message)
            }
        }

        /*devices.value?.let { devices ->
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val req = api.updateDevice(device.name, device)
                    req.await().let {
                        devices[device.name] = it
                    }
                }
                catch (e: Throwable){
                    Log.e("updateDevice", "Oops: Something went wrong. " + e.message)
                }
            }
        }*/
    }
}
