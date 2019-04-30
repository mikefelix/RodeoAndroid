package com.mozzarelly.rodeo.devices

import android.os.Bundle
import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.mozzarelly.rodeo.R
import kotlinx.android.synthetic.main.devices_fragment.*

class DevicesFragment : androidx.fragment.app.Fragment() {
    companion object {
        fun newInstance() = DevicesFragment()
    }

    lateinit var viewModel: DevicesLCE
    lateinit var adapter: DeviceGroupsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DevicesLCE::class.java)
        adapter = DeviceGroupsAdapter(viewModel)

        devicesRecycler.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            refreshDevices()
        }

        viewModel.onBeginLoadingContainer {
            swipeRefresh.isRefreshing = true
        }

        viewModel.onLoadingContainerComplete {
            swipeRefresh.isRefreshing = false
        }

        viewModel.onLoadingElementsComplete {
            swipeRefresh.isRefreshing = false
        }

        viewModel.onErrorLoadingContainer { err ->
            Snackbar.make(devicesRecycler, "Error loading devices: ${err.message}", Snackbar.LENGTH_LONG).show()
        }

//        viewModel.onErrorLoadingElement { name, err ->
//            Snackbar.make(devicesRecycler, "Error loading device $name: ${err.message}", Snackbar.LENGTH_LONG).show()
//        }

        refreshDevices()
    }

    private fun refreshDevices() {
        viewModel.refresh()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_refresh -> {
                swipeRefresh.isRefreshing = true
                refreshDevices()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.devices_menu, menu)
    }


}
