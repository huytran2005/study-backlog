package com.example.brainnote.feature.auth.repository

import kotlinx.coroutines.delay

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun googleLogin(): Result<Unit>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
}

class DefaultAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
        delay(1500) // Simulate network delay
        return Result.success(Unit) // Just for simulation of a correct login
    }

    override suspend fun googleLogin(): Result<Unit> {
        delay(1000) // Simulate SSO delay
        return Result.success(Unit)
    }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> {
        delay(1500)
        return Result.success(Unit)
    }
}
