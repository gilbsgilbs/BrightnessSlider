package fr.netstat.brightnessslider

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val canWritePermission = Settings.System.canWrite(this)
        if (!canWritePermission) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        if (!isAccessibilityServiceEnabled()) {
            val bundle = Bundle().apply {
                val bundleString = "$packageName/${StatusBarAccessibilityService::class.java.name}"
                putString(":settings:fragment_args_key", bundleString)
            }
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                putExtra(":settings:show_fragment_args", bundle)
            }
            startActivity(intent)
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityManager = this.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val enabledServiceInfo = enabledService.resolveInfo.serviceInfo
            if (
                enabledServiceInfo.packageName.equals(packageName) &&
                enabledServiceInfo.name.equals(StatusBarAccessibilityService::class.java.name)
            ) {
                return true
            }
        }
        return false
    }
}
