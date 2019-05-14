package com.mozzarelly.rodeo

import android.os.Bundle
import android.view.Menu
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.mozzarelly.rodeo.alarm.AlarmFragment
import com.mozzarelly.rodeo.devices.DevicesFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var menu: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        appBarConfiguration = AppBarConfiguration(R.menu.navigation, null)
//
//         Set up ActionBar
//        setSupportActionBar(toolbar)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        setSupportActionBar(toolbar)

        loadFragment(DevicesFragment())

        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            return@OnNavigationItemSelectedListener when (item.itemId) {
                R.id.navigation_devices -> loadFragment(DevicesFragment())
                R.id.navigation_alarm -> loadFragment(AlarmFragment())
                R.id.navigation_video -> loadFragment(VideoFragment())
                else -> false
            }
        })
    }

    fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        return true
    }
}
