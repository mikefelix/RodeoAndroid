package com.mozzarelly.rodeo.devices

import android.os.Bundle
import android.view.*
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mozzarelly.rodeo.R
import kotlinx.android.synthetic.main.devices_fragment.*

private fun <T> MutableLiveData<T>.observe(owner: LifecycleOwner, lambda: (T) -> Unit){
    this.observe(owner, Observer(lambda))
}


class DevicesFragment : androidx.fragment.app.Fragment() {
    companion object {
        fun newInstance() = DevicesFragment()
    }

    lateinit var viewModel: DevicesViewModel
    val adapter: DeviceGroupsAdapter = DeviceGroupsAdapter(
        onSwitch = { name, state -> viewModel.updateDevice(Device(name, null, state))
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)

        devicesRecycler.layoutManager = LinearLayoutManager(this.context)
        devicesRecycler.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            refreshDevices()
        }

        viewModel.devices.observe(this) { devices ->
            if (devices != null){
                adapter.groups = devices.groups

                for (entry in viewModel.devicesByName.entries) {
                    entry.value.observe(this, adapter::updateDevice)
                }
            }
        }

        refreshDevices()
    }

    private fun refreshDevices() {
        viewModel.loadDevicesAsync().invokeOnCompletion {
            if (it == null){
                swipeRefresh.isRefreshing = false
            }
            else {
                Snackbar.make(devicesRecycler, it.message ?: "Unknown error", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_refresh -> {
                swipeRefresh.isRefreshing = true
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.devices_menu, menu)
    }


}
