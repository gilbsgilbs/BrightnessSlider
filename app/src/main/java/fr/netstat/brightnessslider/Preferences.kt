package fr.netstat.brightnessslider

import android.content.Context
import androidx.preference.PreferenceManager
import org.greenrobot.eventbus.EventBus

class Preferences(private val context: Context) {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val sharedPrefsEditor = sharedPrefs.edit()

    var isGloballyDisabled
        get() = run {
            sharedPrefs.getBoolean(context.getString(R.string.shared_pref_is_globally_disabled), false)
        }
        set(value) = run {
            sharedPrefsEditor.putBoolean(context.getString(R.string.shared_pref_is_globally_disabled), value)
            sharedPrefsEditor.apply()
            EventBus.getDefault().post(GloballyDisabledChangedEvent(value))
        }
}
