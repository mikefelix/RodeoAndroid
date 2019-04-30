package com.mozzarelly.rodeo

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mozzarelly.rodeo.alarm.model.AlarmTime

data class Time(val hour: String, val minute: String){
    override fun toString() = "$hour:$minute"
}

class TimePickerFragment(val time: AlarmTime,
                         val okListener: (Time) -> Unit,
                         val offListener: () -> Unit,
                         val cancelListener: () -> Unit)
    : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        time.time ?: throw IllegalStateException("No time found.")
        val (hour, minute) = time.time.split(":")

        return TimePickerDialog(activity!!, this, hour.toInt(), minute.toInt(), DateFormat.is24HourFormat(activity)).apply {
            setButton(DialogInterface.BUTTON_NEUTRAL, "Off") { _, _ -> offListener() }
        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        okListener(Time(hourOfDay.toString(), minute.toString()))
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        cancelListener()
    }
}
