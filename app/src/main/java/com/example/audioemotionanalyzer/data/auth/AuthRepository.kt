package com.example.audioemotionanalyzer.data.auth

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    suspend fun register(username: String, password: String): Result<User>
    fun logout()
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): User?
}

data class User(val id: String, val username: String, val token: String)

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}


class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val preferencesManager: PreferencesManager
) : AuthRepository {
    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            val user = authApi.login(username, password)
            preferencesManager.saveUser(user)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun register(username: String, password: String): Result<User> {
        return try {
            val user = authApi.register(username, password)
            preferencesManager.saveUser(user)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun logout() {
        preferencesManager.clearAuth()
    }

    override fun isLoggedIn(): Boolean {
        return preferencesManager.getAuthToken() != null
    }

    override fun getCurrentUser(): User? {
        return preferencesManager.getUser()
    }
}