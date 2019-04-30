package com.mozzarelly.rodeo.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.mozzarelly.rodeo.TimePickerFragment
import com.mozzarelly.rodeo.Utils.observe
import com.mozzarelly.rodeo.databinding.AlarmFragmentBinding


class AlarmFragment : androidx.fragment.app.Fragment() {

    var timePicker: TimePickerFragment? = null

    @Suppress("UNCHECKED_CAST")
    val factory = object: ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AlarmViewModel() as T
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = AlarmFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        binding.model = ViewModelProviders.of(this, factory).get(AlarmViewModel::class.java).also { model ->

            model.onError {
                Snackbar.make(binding.swipeRefresh, "Error loading alarm: ${it.message}", Snackbar.LENGTH_LONG).show()
            }

            binding.swipeRefresh.setOnRefreshListener {
                model.refresh()
            }

            observe(model.editingDay) { time ->
                if (time != null){
                    fragmentManager?.let { fragMan ->
                        timePicker = TimePickerFragment(time,
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

            model.refresh()
        }

        return binding.root
    }


}

