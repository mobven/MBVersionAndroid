package com.mobven.mbversion_android

import android.app.Application
import com.mobven.mb_version.MBVersionOverlay


class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MBVersionOverlay.init(this)
    }
}