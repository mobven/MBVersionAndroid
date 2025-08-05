package com.mobven.mbversion_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        addFragment()
    }

    private fun addFragment() {
        val fragment = BlankFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.container, fragment, "BlankFragment")
            .commit()
    }
}