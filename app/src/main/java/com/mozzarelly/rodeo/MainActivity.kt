package com.mozzarelly.rodeo

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            return@OnNavigationItemSelectedListener when (item.itemId) {
                R.id.navigation_home -> {
                    message.setText(R.string.title_home)
                    true
                }
                R.id.navigation_dashboard -> {
                    message.setText(R.string.title_dashboard)
                    true
                }
                R.id.navigation_notifications -> {
                    message.setText(R.string.title_notifications)
                    true
                }
                else -> false
            }
        })
    }
}
