package com.mobven.mb_version

import android.app.Activity
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

/**
 * A lightweight Android library for displaying app version information as an overlay on all activities.
 * @author Mobven
 * @version 1.0.1
 * @since 1.0.0
 */
object MBVersionOverlay {

    private var isEnabled = false
    private var customText: String? = null
    private var backgroundColor = Color.parseColor("#AA4444FF")
    private var textColor = Color.WHITE
    private var textSize = 16f
    private var position = Position.BOTTOM
    private var bottomMargin = 32
    private var isDraggable = true

    enum class Position {
        TOP, BOTTOM
    }

    /**
     * Initializes the MBVersionOverlay library and registers activity lifecycle callbacks.
     * This method should be called once in the Application class onCreate() method.
     *
     * @param application The Application instance
     * @param enabled Whether the overlay should be enabled.
     *
     * @since 1.0.0
     */
    fun init(
        application: Application,
        enabled: Boolean = true
    ) {
        isEnabled = enabled

        if (isEnabled) {
            application.registerActivityLifecycleCallbacks(MBVersionOverlayLifecycleCallback())
        } else {
            println("MBVersionOverlay: Disabled by user")
        }
    }

    /**
     * Configures the appearance and behavior of the overlay.
     * This method should be called before init() to ensure configuration is applied.
     *
     * @since 1.0.0
     */
    fun configure(
        customText: String? = this.customText,
        backgroundColor: Int = this.backgroundColor,
        textColor: Int = this.textColor,
        textSize: Float = this.textSize,
        position: Position = this.position,
        bottomMargin: Int = this.bottomMargin,
        isDraggable: Boolean = this.isDraggable
    ) {
        this.customText = customText
        this.backgroundColor = backgroundColor
        this.textColor = textColor
        this.textSize = textSize
        this.position = position
        this.bottomMargin = bottomMargin
        this.isDraggable = isDraggable
    }

    /**
     * Manually shows or hides the overlay on a specific activity.
     * This method can be used for fine-grained control over overlay visibility.
     *
     * @param activity The activity to show/hide overlay on
     * @param show True to show overlay, false to hide it
     *
     * @since 1.0.0
     */
    fun show(activity: Activity, show: Boolean = true) {
        if (!isEnabled) return

        if (show) {
            addOverlayToActivity(activity)
        } else {
            removeOverlayFromActivity(activity)
        }
    }

    /**
     * Internal method to add overlay to a specific activity.
     * This method is called automatically by the lifecycle callback or manually via show() method.
     *
     * @param activity The activity to add overlay to
     *
     * @since 1.0.0
     */
    internal fun addOverlayToActivity(activity: Activity) {
        if (!isEnabled) {
            println("MBVersionOverlay: Not enabled, skipping")
            return
        }

        activity.runOnUiThread {
            println("MBVersionOverlay: Adding overlay to ${activity.javaClass.simpleName}")

            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
            if (rootView == null) {
                println("MBVersionOverlay: Root view is null!")
                return@runOnUiThread
            }

            val overlayId = getOverlayId()

            if (rootView.findViewById<View>(overlayId) != null) {
                println("MBVersionOverlay: Overlay already exists")
                return@runOnUiThread
            }

            val overlay = createOverlayView(activity)
            overlay.id = overlayId

            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = when (position) {
                    Position.TOP -> Gravity.TOP
                    Position.BOTTOM -> Gravity.BOTTOM
                }

                if (position == Position.BOTTOM) {
                    bottomMargin = dpToPx(activity, this@MBVersionOverlay.bottomMargin)
                }
            }

            rootView.addView(overlay, params)
            println("MBVersionOverlay: Overlay added successfully")
        }
    }


    /**
     * Internal method to remove overlay from a specific activity.
     * This method is called automatically when activities are destroyed or manually via show() method.
     *
     * @param activity The activity to remove overlay from
     *
     * @since 1.0.0
     */
    internal fun removeOverlayFromActivity(activity: Activity) {
        activity.runOnUiThread {
            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
            val overlay = rootView?.findViewById<View>(getOverlayId())
            overlay?.let {
                rootView.removeView(it)
                println("MBVersionOverlay: Overlay removed from ${activity.javaClass.simpleName}")
            }
        }
    }

    /**
     * Creates the TextView that displays the version overlay.
     * The TextView is configured with current settings and includes click-to-hide functionality.
     * If draggable is enabled, adds touch handling for drag & drop functionality.
     *
     * @param context Context used to create the TextView
     * @return Configured TextView ready to be added to a ViewGroup
     *
     * @since 1.0.0
     */
    private fun createOverlayView(context: Context): TextView {
        return TextView(context).apply {
            text = getDisplayText(context)
            setBackgroundColor(backgroundColor)
            setTextColor(textColor)
            textSize = this@MBVersionOverlay.textSize
            setPadding(16, 8, 16, 8)
            gravity = Gravity.CENTER

            elevation = 100f
            translationZ = 100f

            if (isDraggable) {
                setupDragAndDrop(this)
            } else {
                setOnClickListener {
                    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
                }
            }
        }
    }

    /**
     * Generates the display text for the overlay.
     * If customText is set, it returns that. Otherwise, generates version info from PackageManager.
     *
     * @param context Context used to access PackageManager
     * @return String to display in the overlay, format: "v{versionName}({versionCode})"
     *
     * @since 1.0.0
     *
     * Examples of returned strings:
     * - "v1.2.3(45)"
     * - "v2.0.0-beta(123)"
     * - "QA Build v2.1" (when customText is set)
     * - "Version Info Error" (when PackageInfo cannot be retrieved)
     */
    private fun getDisplayText(context: Context): String {
        return customText ?: run {
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val versionName = packageInfo.versionName ?: "Unknown"
                val versionCode = getLongVersionCode(packageInfo)
                "v$versionName($versionCode)"
            } catch (e: Exception) {
                println("MBVersionOverlay: Error getting version info: ${e.message}")
                customText ?: "Version Info Error"
            }
        }
    }

    /**
     * Safely extracts version code from PackageInfo, handling API level differences.
     * Uses longVersionCode for API 28+ and falls back to deprecated versionCode for older APIs.
     *
     * @param packageInfo PackageInfo object obtained from PackageManager
     * @return Version code as Long, or 0L if extraction fails
     *
     * @since 1.0.0
     */
    private fun getLongVersionCode(packageInfo: PackageInfo): Long {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            println("MBVersionOverlay: Error getting version code: ${e.message}")
            0L
        }
    }

    /**
     * Sets up drag and drop functionality for the overlay view.
     * Allows users to drag the overlay to any position on screen.
     *
     * @param view The TextView to make draggable
     *
     * @since 1.0.1
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupDragAndDrop(view: TextView) {
        var dX = 0f
        var dY = 0f
        var lastAction = 0

        view.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    lastAction = android.view.MotionEvent.ACTION_DOWN
                    true
                }

                android.view.MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY

                    val parent = v.parent as? ViewGroup
                    parent?.let { parentView ->
                        val maxX = parentView.width - v.width
                        val maxY = parentView.height - v.height

                        v.x = newX.coerceIn(0f, maxX.toFloat())
                        v.y = newY.coerceIn(0f, maxY.toFloat())
                    }

                    lastAction = android.view.MotionEvent.ACTION_MOVE
                    true
                }

                android.view.MotionEvent.ACTION_UP -> {
                    if (lastAction == android.view.MotionEvent.ACTION_DOWN) {
                        v.visibility = if (v.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                    } else {
                        snapToEdge(v)
                    }
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Snaps the overlay to the nearest edge of the screen after dragging.
     * This provides a cleaner user experience by avoiding overlays in the middle of content.
     *
     * @param view The view to snap to edge
     *
     * @since 1.0.1
     */
    private fun snapToEdge(view: View) {
        val parent = view.parent as? ViewGroup ?: return

        val parentWidth = parent.width
        val parentHeight = parent.height
        val viewWidth = view.width
        val viewHeight = view.height

        val currentX = view.x
        val currentY = view.y

        val distanceToRight = parentWidth - currentX - viewWidth
        val distanceToBottom = parentHeight - currentY - viewHeight

        when (minOf(currentX, distanceToRight, currentY, distanceToBottom)) {
            currentX -> {
                view.animate().x(0f).setDuration(200).start()
            }

            distanceToRight -> {
                view.animate().x((parentWidth - viewWidth).toFloat()).setDuration(200).start()
            }

            currentY -> {
                view.animate().y(0f).setDuration(200).start()
            }

            distanceToBottom -> {
                view.animate().y((parentHeight - viewHeight).toFloat()).setDuration(200).start()
            }
        }
    }


    /**
     * Converts DP to pixels based on device density.
     *
     * @param context Context to get display metrics
     * @param dp DP value to convert
     * @return Pixel value
     *
     * @since 1.0.1
     */
    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    /**
     * Generates a unique ID for the overlay view to prevent duplicates and enable easy retrieval.
     * Uses hash code of a constant string to ensure consistent ID across app lifecycle.
     *
     * @return Unique integer ID for the overlay view
     *
     * @since 1.0.0
     */
    private fun getOverlayId(): Int {
        return "mb_version_overlay_view".hashCode()
    }
}