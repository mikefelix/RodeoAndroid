package com.mozzarelly.rodeo.alarm

import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mozzarelly.rodeo.LCE
import com.mozzarelly.rodeo.Time
import com.mozzarelly.rodeo.alarm.model.Alarm
import com.mozzarelly.rodeo.alarm.model.AlarmOverride
import com.mozzarelly.rodeo.alarm.model.AlarmTime
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class AlarmViewModel : LCE<Alarm>(), Observable {

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

    val editingDay = liveData<AlarmTime>()
    val readout = liveData<String>()

    companion object {
        val api: AlarmApi = Retrofit.Builder()
            .baseUrl("https://mozzarelly.com/home/")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(
                OkHttpClient.Builder().addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                ).build())
            .build()
            .create(AlarmApi::class.java)
    }

    inner class AlarmDayViewModel : ViewModel() {
        var model: AlarmTime? = null
            set(value) {
                field = value
                day.value = field?.day
                time.value = field?.time ?: "off"
                oddWeek.value = field?.oddWeek
                highlighted.value = data()?.let { alarm ->
                    val (today, todayWeek) = alarm.today.split("_")
                    today.toLowerCase() == model?.day?.toLowerCase() && (todayWeek == "odd") == model?.oddWeek
                }
            }

        val day = liveData<String>()
        val time = liveData<String>()
        val enabled = liveData<Boolean>()
        val oddWeek = liveData<Boolean>()
        val highlighted = liveData<Boolean>()

        fun editDay() {
            editingDay.value = model
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
            if (alarm.next.enabled) {
                "The alarm is set to ring ${alarm.next.day} at ${alarm.next.time}."
            }
            else {
                "The alarm is disabled ${alarm.next.day}."
            }
        }
    }

    fun cancelEdit() {
        editingDay.value = null
    }

    fun saveTime(time: Time?){
        editingDay.value = time?.let { editingDay.value?.copy(time = it.toString()) }
    }

    //    override suspend fun doRefresh() = api.getAlarm().await()
    override suspend fun doRefresh() = Alarm(
        false, AlarmTime("tomorrow", false, "07:00", true),
        "07:00", AlarmOverride(true, null, 2), false, "", null,
        arrayOf(null, "07:00", "07:00", "07:00", "07:00", "07:00", "07:00", "09:00", "07:00", "07:00", "07:00", "07:00", "07:00", "07:00"),
        "friday_even"
    )

    override suspend fun doUpdate(x: Alarm) = api.setAlarm(x).await()

    override fun afterRefresh() {
        val days = data()?.days()/*?.map { AlarmDayViewModel(it) }*/ ?: listOf()
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
    }

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(
        callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.remove(callback)
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    fun <T> T.toLiveData(): MutableLiveData<T> = MutableLiveData(this)

    fun <T> liveData(t: T? = null): MutableLiveData<T?> = MutableLiveData(t)

}