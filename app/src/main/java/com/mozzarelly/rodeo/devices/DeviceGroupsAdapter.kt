package com.mozzarelly.rodeo.devices

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import com.mozzarelly.rodeo.R
import android.widget.ArrayAdapter
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.android.synthetic.main.device.view.*

typealias OnSwitch = (String, Boolean) -> Unit

class DeviceGroupsAdapter(val onSwitch: OnSwitch) :
    androidx.recyclerview.widget.RecyclerView.Adapter<DeviceGroupViewHolder>() {

    var groups: List<DeviceGroup> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val viewHolders: MutableMap<String,DeviceGroupViewHolder> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceGroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.device_group, parent, false)
        return DeviceGroupViewHolder(view, onSwitch)
    }

    override fun onBindViewHolder(holder: DeviceGroupViewHolder, position: Int) {
        holder.group = groups[position]
        groups[position].devices.forEach { viewHolders[it] = holder}
    }

    override fun getItemCount() = groups.size


    fun updateDevice(device: Device){
        viewHolders[device.name]?.updateDevice(device)
    }
}

class DeviceGroupViewHolder(val view: View, val onSwitch: OnSwitch) : ViewHolder(view) {
    var adapter: DevicesAdapter? = null

    fun updateDevice(device: Device){
        adapter?.run{
            viewsByDevice[device.name]?.run {
                findViewById<Switch>(R.id.on_switch).run {
                    isEnabled = true
                    isChecked = device.on
                }
            }
        }
    }

    var group: DeviceGroup? = null
        set(group) {
            field = group
            if (group != null){
                view.findViewById<TextView>(R.id.name).text = group.desc()

                view.findViewById<GridView>(R.id.devices).adapter = DevicesAdapter(view.context, group.devices.map { Device(it) }, onSwitch).also {
                    this.adapter = it
                }
            }
        }
}

class DevicesAdapter(context: Context, val devices: List<Device>, val onSwitch: OnSwitch) :
    ArrayAdapter<Device>(context, android.R.layout.simple_list_item_1, devices.toTypedArray()) {

    val viewsByDevice: MutableMap<String, View> = mutableMapOf()

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        return view ?: LayoutInflater.from(parent.context).inflate(R.layout.device, parent, false).apply {
            val item = devices[position]
            viewsByDevice[item.name] = this

//            findViewById<TextView>(R.id.name).text = item.desc()
            name.text = item.desc()

//            findViewById<Switch>(R.id.on_switch)?.apply {
            on_switch.apply {
                isChecked = item.on
                setOnCheckedChangeListener { _, isChecked ->
//                    this is running twice?
                    onSwitch(item.name, isChecked)
                }
            }

        }
    }
}