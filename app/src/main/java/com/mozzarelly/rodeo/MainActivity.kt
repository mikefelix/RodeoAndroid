package com.mozzarelly.rodeo

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.mozzarelly.rodeo.alarm.AlarmFragment
import com.mozzarelly.rodeo.devices.DevicesFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
//    private lateinit var navController: NavController
//    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        navController = Navigation.findNavController(this, R.id.fragment_container)
//        appBarConfiguration = AppBarConfiguration(R.menu.navigation, null)
        setSupportActionBar(toolbar)

        loadFragment(AlarmFragment())

        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            return@OnNavigationItemSelectedListener when (item.itemId) {
                R.id.navigation_devices -> loadFragment(DevicesFragment())
                R.id.navigation_alarm -> loadFragment(AlarmFragment())
                R.id.navigation_video -> loadFragment(VideoFragment())
                else -> false
            }
        })
    }

    fun loadFragment(fragment: androidx.fragment.app.Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        return true
    }
}
