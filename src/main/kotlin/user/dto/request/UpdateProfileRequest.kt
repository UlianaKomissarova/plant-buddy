package dev.uliana.user.dto.request

data class UpdateProfileRequest(
    val name: String?,
    val fcmToken: String?,
    val timezone: String?,
    /** Preferred notification time in "HH:mm" format (in user's timezone). */
    val notificationTime: String?,
)
