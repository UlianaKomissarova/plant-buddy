package dev.uliana.user.mapper

import dev.uliana.user.database.UserEntity
import dev.uliana.user.model.UserInfo

fun UserEntity.toUserInfo(): UserInfo = UserInfo(
    userId = this.id.value,
    email = this.email,
    isConfirmed = this.isConfirmed,
    isBlocked = this.isBlocked,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    deletedAt = this.deletedAt,
)
