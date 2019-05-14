package com.mozzarelly.rodeo.alarm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mozzarelly.rodeo.LCE
import com.mozzarelly.rodeo.alarm.model.*
import com.squareup.moshi.Moshi
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class AlarmViewModel : LCE<Alarm>() {

    val monday1 = AlarmDayViewModel()
    val tuesday1 = AlarmDayViewModel()
    val wednesday1 = AlarmDayViewModel()
    val thursday1 = AlarmDayViewModel()
    val friday1 = AlarmDayViewModel()
    val saturday1 = AlarmDayViewModel()
    val sunday1 = AlarmDayViewModel()
    val monday2 = AlarmDayViewModel()
    val tuesday2 = AlarmDayViewModel()
    val wednesday2 = AlarmDayViewModel()
    val thursday2 = AlarmDayViewModel()
    val friday2 = AlarmDayViewModel()
    val saturday2 = AlarmDayViewModel()
    val sunday2 = AlarmDayViewModel()

    val editingDay = liveData<AlarmDayViewModel>()
    val disableFor = liveData<Int>()
    val readout = liveData<String>()

    var next: AlarmDayViewModel? = null

    companion object {
        val api: AlarmApi = Retrofit.Builder()
            .baseUrl("https://mozzarelly.com/home/")
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder()
                .add(TimeAdapter())
                .build()
            ))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(
                OkHttpClient.Builder().addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                ).build())
            .build()
            .create(AlarmApi::class.java)
    }

    inner class AlarmDayViewModel : ViewModel() {
        var model: AlarmSetting? = null
            set(value) {
                field = value
                day.value = field?.dayName()
                time.value = field?.time?.toString() ?: "off"
                if (data()?.today == model?.day) {
                    highlighted.value = true
                }
            }

        val day = liveData<String>()
        val time = liveData<String>()
        val enabled = liveData<Boolean>()
        val highlighted = liveData<Boolean>()

        fun editDay() {
            editingDay.value = this
        }
    }

    private fun alarmText(): String {
        val alarm = data() ?: return ""

        return if (alarm.on) {
            "The alarm is ringing. Wake up and brew the coffee!"
        }
        else if (alarm.override != null) {
            if (alarm.override.disable == true && alarm.override.days > 0) {
                "The alarm is disabled for ${alarm.override.days} day${if (alarm.override.days == 1) "" else "s"}."
            }
            else if (alarm.override.time != null) {
                "The alarm is overridden to ring at ${alarm.next.time} ${alarm.next.day}."
            }
            else {
                "The alarm is overridden but it's not clear how."
            }
        }
        else {
            if (alarm.next.enabled()) {
                val day = when (alarm.next.day){
                    alarm.today -> "today"
                    (alarm.today + 1) % 14 -> "tomorrow"
                    else -> DayNames.values()[alarm.next.day].toString()
                }

                "The alarm is set to ring $day at ${alarm.next.time}."
            }
            else {
                "The alarm is disabled."
            }
        }
    }

    fun cancelEdit() {
        editingDay.value = null
    }

    fun saveDisabled(days: Int){
        try {
            viewModelScope.launch {
                api.disable(days).await()
            }
        }
        catch (e: Exception) {
            postError(e)
        }
    }

    fun saveTime(time: Time?){
        val day = editingDay.value ?: return
        val newModel = day.model?.copy(time = time) ?: throw RuntimeException("No model is being edited?")
        day.model = newModel

        try {
            viewModelScope.launch {
                val action = if (day == next)
                    api.saveOverride(newModel.time?.toString() ?: "off")
                else
                    api.saveSetting(newModel.day.toString(), newModel.time?.toString() ?: "off")

                action.await()
            }
        }
        catch (e: Exception) {
            postError(e)
        }
        finally {
            editingDay.value = null
        }
    }

    fun showOverrideNextDialog(){
        editingDay.value = next
    }

    fun showDisableDialog(){
        data()?.override?.run {
            disableFor.value = if (disable == true) days else 1
        }
    }

    override suspend fun doRefresh() = api.getAlarm().await()

    override suspend fun doUpdate(x: Alarm) = api.setAlarm(x).await()

    override fun afterRefresh() {
        val days = data()?.days() ?: listOf()
        monday1.model = days[0]
        tuesday1.model = days[1]
        wednesday1.model = days[2]
        thursday1.model = days[3]
        friday1.model = days[4]
        saturday1.model = days[5]
        sunday1.model = days[6]
        monday2.model = days.getOrElse(7) { days[0] }
        tuesday2.model = days.getOrElse(8) { days[1] }
        wednesday2.model = days.getOrElse(9) { days[2] }
        thursday2.model = days.getOrElse(10) { days[3] }
        friday2.model = days.getOrElse(11) { days[4] }
        saturday2.model = days.getOrElse(12) { days[5] }
        sunday2.model = days.getOrElse(13) { days[6] }

        readout.value = alarmText()

        next = AlarmDayViewModel().apply { model = data()?.next }
    }

    fun <T> T?.toLiveData(): MutableLiveData<T> = MutableLiveData<T>(this)

    fun <T> liveData(t: T? = null): MutableLiveData<T?> = MutableLiveData(t)

}