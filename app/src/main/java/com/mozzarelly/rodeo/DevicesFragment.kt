package com.mozzarelly.rodeo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.devices_fragment.*

class DevicesFragment : Fragment() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var viewAdapter: DevicesAdapter
//    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: DevicesViewModel

    companion object {
        fun newInstance() = DevicesFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devices_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DevicesViewModel::class.java)
        viewModel.getDevices().observe(this, Observer<List<Device>> { devices ->

        })
    }

}
