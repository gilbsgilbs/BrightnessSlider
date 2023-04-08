package fr.netstat.brightnessslider

import android.accessibilityservice.AccessibilityService
import android.content.res.Resources
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import kotlin.math.abs

class StatusBarAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        @Suppress("DEPRECATION")
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            getStatusBarHeight(),
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
//            or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE  // <= SDK30 ??
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP
        }

        val layout = View(this)

        val minXDistance = 100f // Minimum sliding distance to start changing the brightness
        var firstXValue = 0f // Position at which the user started touching the status bar
        var isSliding = false // Whether the user is currently sliding on the status bar
        val padding = 100 // Left and right "deadzone"
        var startTouchTime = 0L // Epoch at which the user started touching the status bar
        val maxTouchDelay = 300 // Maximum delay between two touches to trigger a screen lock
        layout.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isSliding = false
                    firstXValue = event.x

                    val currentTime = System.currentTimeMillis()
                    if (currentTime - startTouchTime < maxTouchDelay) {
                        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
                    }
                    startTouchTime = currentTime
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isSliding && minXDistance <= abs(event.x - firstXValue)) {
                        isSliding = true
                    }
                    if (isSliding) {
                        val totalWidth = getScreenWidth()
                        val brightnessValue = (
                            255 * (event.x - padding) / (totalWidth - 2 * padding)
                            ).toInt().coerceIn(0, 255)

                        Settings.System.putInt(
                            contentResolver,
                            Settings.System.SCREEN_BRIGHTNESS,
                            brightnessValue,
                        )
                    }
                }
            }
            false
        }

        windowManager.addView(layout, params)
    }

    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId != 0) resources.getDimensionPixelSize(resourceId) else 120
    }

    private fun getScreenWidth() = Resources.getSystem().displayMetrics.widthPixels

    override fun onAccessibilityEvent(event: AccessibilityEvent) {}
    override fun onInterrupt() {}
}
