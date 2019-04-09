package com.mozzarelly.rodeo

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mozzarelly.rodeo.devices.DevicesLCE

class VideoFragment : androidx.fragment.app.Fragment() {
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var viewAdapter: DeviceGroupsAdapter
//    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewModel: DevicesLCE

    companion object {
        fun newInstance() = VideoFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.alarm_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DevicesLCE::class.java)
    }

}
