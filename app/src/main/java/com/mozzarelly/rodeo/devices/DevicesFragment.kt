package com.mozzarelly.rodeo.devices

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.mozzarelly.rodeo.R
import com.mozzarelly.rodeo.RodeoFragment
import com.mozzarelly.rodeo.Utils
import kotlinx.android.synthetic.main.devices_fragment.*

class DevicesFragment : RodeoFragment() {
    companion object {
        fun newInstance() = DevicesFragment()
    }

    lateinit var viewModel: DevicesLCE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false).also {
            setHasOptionsMenu(true)
            rootView = it
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DevicesLCE::class.java).apply {
            onBeginLoadingContainer {
                swipeRefresh.isRefreshing = true
            }

            onLoadingContainerComplete {
                swipeRefresh.isRefreshing = false
            }

            onLoadingElementsComplete {
                swipeRefresh.isRefreshing = false
            }

            onErrorLoadingContainer { err ->
                Snackbar.make(devicesRecycler, "Error loading devices: ${err.message}", Snackbar.LENGTH_LONG).show()
            }
        }

        devicesRecycler.adapter = DeviceGroupsAdapter(viewModel)

        swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.refresh()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_refresh -> {
                swipeRefresh.isRefreshing = true
                viewModel.refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.devices_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

}
