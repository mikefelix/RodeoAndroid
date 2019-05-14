package com.mozzarelly.rodeo.alarm

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.mozzarelly.rodeo.RodeoFragment
import com.mozzarelly.rodeo.TimePickerFragment
import com.mozzarelly.rodeo.Utils.observe
import com.mozzarelly.rodeo.alarm.model.AlarmSetting
import com.mozzarelly.rodeo.alarm.model.Time
import com.mozzarelly.rodeo.databinding.AlarmFragmentBinding
import android.view.*
import com.mozzarelly.rodeo.NumberPickerFragment
import com.mozzarelly.rodeo.R


class AlarmFragment : RodeoFragment() {

    var timePicker: TimePickerFragment? = null
    var numberPicker: NumberPickerFragment? = null
    lateinit var viewModel: AlarmViewModel

    @Suppress("UNCHECKED_CAST")
    val factory = object: ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AlarmViewModel() as T
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        AlarmFragmentBinding.inflate(inflater, container, false).run {
            setHasOptionsMenu(true)
            lifecycleOwner = this@AlarmFragment
            rootView = this.root
            viewModel = ViewModelProviders.of(this@AlarmFragment, factory).get(AlarmViewModel::class.java).also { model ->
                model.onError {
                    Snackbar.make(swipeRefresh, "Error loading alarm: ${it.message}", Snackbar.LENGTH_LONG)
                        .show()
                }

                observe(model.editingDay) { dayEditing ->
                    if (dayEditing != null) {
                        fragmentManager?.let { fragMan ->
                            timePicker = TimePickerFragment(
                                setting = dayEditing.model ?: AlarmSetting(Time.fromString("8:00")),
                                title = "Edit " + (dayEditing.day.value ?: "time"),
                                okListener = { model.saveTime(it) },
                                offListener = { model.saveTime(null) },
                                cancelListener = { model.editingDay.value = null }
                            ).apply {
                                show(fragMan, "timePicker")
                            }
                        }
                    }
                    else {
                        timePicker?.dismiss()
                    }
                }

                observe(model.disableFor) { days ->
                    if (days == null) {
                        numberPicker?.dismiss()
                    }
                    else {
                        fragmentManager?.let { fragMan ->
                            numberPicker = NumberPickerFragment(
                                title = "Disable alarm",
                                message = "Disable for how many days?",
                                setting = days,
                                okListener = { model.saveDisabled(it) },
                                cancelListener = { model.disableFor.value = null }
                            ).apply {
                                show(fragMan, "numberPicker")
                            }
                        }
                    }
                }

                model.refresh()
            }

            model = viewModel
        }

        return rootView
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.action_override -> {
            viewModel.showOverrideNextDialog()
            true
        }
        R.id.action_disable -> {
            viewModel.showDisableDialog()
            true
        }
        else -> false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.alarm_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}

