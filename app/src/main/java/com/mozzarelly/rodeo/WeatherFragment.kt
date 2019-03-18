package com.mozzarelly.rodeo

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mozzarelly.rodeo.devices.Device
import com.mozzarelly.rodeo.devices.DevicesViewModel

class WeatherFragment : androidx.fragment.app.Fragment() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var viewAdapter: DeviceGroupsAdapter
//    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: DevicesViewModel

    companion object {
        fun newInstance() = WeatherFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
    }

}
