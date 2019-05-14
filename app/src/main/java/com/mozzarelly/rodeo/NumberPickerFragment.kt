package com.mozzarelly.rodeo

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mozzarelly.rodeo.alarm.model.AlarmSetting

class NumberPickerFragment(val title: String,
                           val message: String,
                           val setting: Int,
                           val okListener: (Int) -> Unit,
                           val cancelListener: () -> Unit)
    : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(requireContext()).apply {
        setView(NumberPicker(requireContext()).apply {
            maxValue = 14
            minValue = 0
            value = setting
        })
        setTitle(title)
        setMessage(message)
        setPositiveButton("Set", { dialog, num -> okListener(num) })
        setNegativeButton("Cancel", { dialog, _ -> cancelListener() })
    }.create()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        cancelListener()
    }
}
