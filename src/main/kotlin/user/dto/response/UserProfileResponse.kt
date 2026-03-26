package dev.uliana.user.dto.response

import java.util.UUID

data class UserProfileResponse(
    val id: UUID,
    val email: String,
    val name: String?,
    val timezone: String,
    val notificationTime: String,
    val fcmToken: String?,
)
