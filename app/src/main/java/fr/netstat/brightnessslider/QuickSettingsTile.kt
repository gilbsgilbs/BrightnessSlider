package fr.netstat.brightnessslider

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class QuickSettingsTile : TileService() {
    private lateinit var preferences: Preferences

    override fun onCreate() {
        super.onCreate()
        preferences = Preferences(this)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateQsTileState()
    }

    private fun updateQsTileState() {
        val accessibilityService = StatusBarAccessibilityService.instance
        qsTile.state = when {
            accessibilityService == null -> Tile.STATE_UNAVAILABLE
            preferences.isGloballyDisabled -> Tile.STATE_INACTIVE
            else -> Tile.STATE_ACTIVE
        }
        qsTile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        preferences.isGloballyDisabled = !preferences.isGloballyDisabled
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGloballyDisabledChange(event: GloballyDisabledChangedEvent) {
        updateQsTileState()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAccessibilityStatusChange(event: AccessibilityStatusChangedEvent) {
        updateQsTileState()
    }
}
