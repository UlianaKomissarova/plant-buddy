package dev.uliana.di

import dev.uliana.auth.repository.AuthRepository
import dev.uliana.auth.repository.CodeRepository
import dev.uliana.auth.repository.RefreshTokenRepository
import dev.uliana.auth.repository.RefreshTokenRepositoryImpl
import dev.uliana.auth.security.JwtService
import dev.uliana.auth.service.RegistrationService
import dev.uliana.auth.service.SessionService
import dev.uliana.plant.repository.PlantRepository
import dev.uliana.plant.repository.PlantRepositoryImpl
import dev.uliana.plant.repository.WateringLogRepository
import dev.uliana.plant.repository.WateringLogRepositoryImpl
import dev.uliana.plant.service.PlantService
import dev.uliana.plant.service.WateringService
import dev.uliana.user.email.repository.EmailLogRepository
import dev.uliana.user.email.repository.EmailLogRepositoryImpl
import dev.uliana.user.email.service.EmailBuilder
import dev.uliana.user.email.service.HtmlEmailBuilderImpl
import org.koin.dsl.module

val appModule = module {
    // Auth
    single { AuthRepository() }
    single { CodeRepository() }
    single<RefreshTokenRepository> { RefreshTokenRepositoryImpl() }
    single<EmailLogRepository> { EmailLogRepositoryImpl() }
    single<EmailBuilder> { HtmlEmailBuilderImpl() }
    single { JwtService(get()) }
    single { RegistrationService(get(), get(), get()) }
    single { SessionService(get(), get()) }

    // Plants
    single<PlantRepository> { PlantRepositoryImpl() }
    single<WateringLogRepository> { WateringLogRepositoryImpl() }
    single { PlantService(get()) }
    single { WateringService(get(), get(), get()) }
}
