package com.mozzarelly.rodeo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

class DevicesAdapter(private var devices: List<Device>) : RecyclerView.Adapter<DevicesAdapter.DevicesViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class DevicesViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    fun setDevices(devices: List<Device>) {
        this.devices = devices
        notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DevicesAdapter.DevicesViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context).inflate(R.layout.device, parent, false) as TextView

        // set the view's size, margins, paddings and layout parameters
        return DevicesViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.text = devices[position].name
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = devices.size
}