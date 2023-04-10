package fr.netstat.brightnessslider

data class GloballyDisabledChangedEvent(val newValue: Boolean)

enum class AccessibilityStatusType { BOUND, UNBOUND }
data class AccessibilityStatusChangedEvent(val newValue: AccessibilityStatusType)
