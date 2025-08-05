package com.mobven.mb_version

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Internal lifecycle callback implementation for automatically managing version overlays
 * across all activities in the application.
 *
 * This class implements Application.ActivityLifecycleCallbacks to listen for activity
 * lifecycle events and automatically add/remove version overlays as activities are
 * created and destroyed.
 *
 * The callback is registered automatically when MBVersionOverlay.init() is called
 * and handles overlay management without requiring manual intervention from developers.
 *
 * @since 1.0.0
 * @see MBVersionOverlay.init
 */
internal class MBVersionOverlayLifecycleCallback : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        MBVersionOverlay.addOverlayToActivity(activity)
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        MBVersionOverlay.removeOverlayFromActivity(activity)
    }
}