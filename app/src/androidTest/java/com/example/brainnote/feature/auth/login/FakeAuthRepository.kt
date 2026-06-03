package com.example.brainnote.feature.auth.login

import com.example.brainnote.feature.auth.repository.AuthRepository
import kotlinx.coroutines.delay

class FakeAuthRepository : AuthRepository {
    var shouldReturnError = false
    var delayMillis = 0L

    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(delayMillis)
        return if (shouldReturnError) {
            Result.failure(Exception("Login failed"))
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun googleLogin(): Result<Unit> {
        delay(delayMillis)
        return if (shouldReturnError) {
            Result.failure(Exception("Google Login failed"))
        } else {
            Result.success(Unit)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> {
        delay(delayMillis)
        return if (shouldReturnError) {
            Result.failure(Exception("Registration failed"))
        } else {
            Result.success(Unit)
        }
    }
}
