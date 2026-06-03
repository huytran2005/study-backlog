package com.example.brainnote.feature.auth.repository

import kotlinx.coroutines.delay

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun googleLogin(): Result<Unit>
}

class DefaultAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(1500) // Simulate network delay
        return if (email == "user@brainnote.com" && password == "mypassword") {
             Result.success(Unit) // Just for simulation of a correct login, but usually true for all in simulation
        } else {
             Result.success(Unit) // We will return success for simulation just like before
        }
    }

    override suspend fun googleLogin(): Result<Unit> {
        delay(1000) // Simulate SSO delay
        return Result.success(Unit)
    }
}
