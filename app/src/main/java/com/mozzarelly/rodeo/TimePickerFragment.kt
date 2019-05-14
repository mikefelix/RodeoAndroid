package com.mozzarelly.rodeo

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.mozzarelly.rodeo.alarm.model.AlarmSetting
import com.mozzarelly.rodeo.alarm.model.Time

class TimePickerFragment(val setting: AlarmSetting,
                         val title: String,
                         val okListener: (Time) -> Unit,
                         val offListener: () -> Unit,
                         val cancelListener: () -> Unit)
    : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setting.time ?: throw IllegalStateException("No setting found.")
        val (hour, minute) = setting.time

        return TimePickerDialog(requireContext(), this,
            hour.toInt(), minute.toInt(), DateFormat.is24HourFormat(activity)).apply {
                setButton(DialogInterface.BUTTON_NEUTRAL, "Off") { _, _ -> offListener() }
                setTitle(title)
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
