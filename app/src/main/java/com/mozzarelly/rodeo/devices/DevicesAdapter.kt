package com.mozzarelly.rodeo.devices

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.mozzarelly.rodeo.ListLCE
import com.mozzarelly.rodeo.R
import com.mozzarelly.rodeo.devices.model.Device
import com.mozzarelly.rodeo.devices.model.DevicesContainer
import kotlinx.android.synthetic.main.device.view.*
import kotlinx.android.synthetic.main.device_group_label.view.*
import kotlinx.android.synthetic.main.device_row.view.*



class DeviceGroupsAdapter(val viewModel: ListLCE<DevicesContainer, Device, DeviceRow>) : Adapter<DeviceGroupsAdapter.DeviceRowViewHolder>() {

    init {
        viewModel.onLoadingContainerComplete {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceRowViewHolder = when (viewType) {
        GroupLabel.viewType -> DeviceRowViewHolder.GroupLabel(
            LayoutInflater.from(parent.context).inflate(R.layout.device_group_label, parent, false)
        )
        DevicePair.viewType -> DeviceRowViewHolder.DevicePair(
            LayoutInflater.from(parent.context).inflate(R.layout.device_row, parent, false)
        )
        else -> throw RuntimeException("Can't inflate view type $viewType")
    }

    override fun onBindViewHolder(holder: DeviceRowViewHolder, position: Int) {
        when (holder) {
            is DeviceRowViewHolder.GroupLabel -> {
                val row = viewModel.rows[position] as GroupLabel
                holder.itemView.groupName.text = row.name.capitalize()
            }

            is DeviceRowViewHolder.DevicePair -> {
                val (first, second) = viewModel.rows[position] as DevicePair

                holder.itemView.apply {
                    firstDeviceView.apply {
                        decorateWith(first, true)
                        isEnabled = false
                    }

                    secondDeviceView.apply {
                        decorateWith(second, true)
                        isEnabled = false
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = viewModel.rows.size

    override fun getItemViewType(position: Int): Int = viewModel.rows[position].viewType

    private fun View.decorateWith(device: Device?, initializing: Boolean = false){
        if (device == null) {
            visibility = View.GONE
        }
        else {
            progress.visibility = GONE

            name.apply {
                text = (device.alias ?: device.name).capitalize()

                if (device.overridden) {
                    println("paintFlags before override: $paintFlags")
                    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    println("paintFlags after override: $paintFlags")
                }
                else {
                    println("paintFlags before unoverride: $paintFlags")
                    paintFlags = paintFlags and (Int.MAX_VALUE xor Paint.UNDERLINE_TEXT_FLAG)
                    println("paintFlags after unoverride: $paintFlags")
                }

                if (device.offline)
                    setTextColor(resources.getColor(R.color.colorTextDisabled, null))
                else
                    setTextColor(resources.getColor(R.color.colorText, null))

                setOnClickListener {
                    viewModel.refreshElement(device.name)
                }
            }

            onSwitch.apply {
                visibility = VISIBLE
                isEnabled = !device.offline
                isChecked = device.on
                if (initializing) {
                    setOnCheckedChangeListener { button, isChecked ->
                        if (button.isPressed)
                            viewModel.updateElement(device.copy(on = isChecked))
                    }
                }
            }

            if (initializing) {
                viewModel.onBeginLoadingElement(device.name) {
//                    name.setTextColor(resources.getColor(R.color.colorTextDisabled, null))
                    isEnabled = false
                    onSwitch.visibility = GONE
                    progress.visibility = VISIBLE
                }

                viewModel.onLoadingElementComplete(device.name) {
                    decorateWith(it)
                }

                viewModel.onErrorLoadingElement(device.name) {
                    decorateWith(device.copy(offline = true))
                }

                viewModel.refreshElement(device)
            }

        }
    }

    sealed class DeviceRowViewHolder(view: View) : ViewHolder(view) {
        class GroupLabel(view: View) : DeviceRowViewHolder(view)
        class DevicePair(val view: View) : DeviceRowViewHolder(view)
    }
}