package dev.uliana.user.service

import dev.uliana.user.database.UserEntity
import dev.uliana.user.dto.request.UpdateProfileRequest
import dev.uliana.user.dto.response.UserProfileResponse
import dev.uliana.user.repository.UpdateUserDto
import dev.uliana.user.repository.UserRepository
import dev.uliana.util.exception.BadRequestException
import dev.uliana.util.exception.NotFoundException
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserService(private val repository: UserRepository) {

    suspend fun getProfile(userId: UUID): UserProfileResponse = withContext(Dispatchers.IO) {
        val user = repository.findById(userId) ?: throw NotFoundException("User not found")
        user.toResponse()
    }

    suspend fun updateProfile(userId: UUID, request: UpdateProfileRequest): UserProfileResponse =
        withContext(Dispatchers.IO) {
            request.validate()
            repository.update(
                id = userId,
                dto = UpdateUserDto(
                    name = request.name,
                    fcmToken = request.fcmToken,
                    timezone = request.timezone,
                    notificationTime = request.notificationTime,
                ),
            )?.toResponse() ?: throw NotFoundException("User not found")
        }

    private fun UpdateProfileRequest.validate() {
        notificationTime?.let {
            val regex = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
            if (!it.matches(regex)) throw BadRequestException("notificationTime must be in HH:mm format (e.g. 09:00)")
        }
    }

    private fun UserEntity.toResponse() = UserProfileResponse(
        id = id.value,
        email = email,
        name = name,
        timezone = timezone,
        notificationTime = notificationTime,
        fcmToken = fcmToken,
    )
}
