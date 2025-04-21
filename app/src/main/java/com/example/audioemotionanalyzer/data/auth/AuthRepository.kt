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

// Create the PreferencesManager class
class PreferencesManager(private val context: android.content.Context) {
    private val sharedPreferences = context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun saveUser(user: User) {
        sharedPreferences.edit()
            .putString("user_id", user.id)
            .putString("username", user.username)
            .putString("token", user.token)
            .apply()
    }

    fun getUser(): User? {
        val id = sharedPreferences.getString("user_id", null) ?: return null
        val username = sharedPreferences.getString("username", null) ?: return null
        val token = sharedPreferences.getString("token", null) ?: return null

        return User(id, username, token)
    }

    fun clearAuth() {
        sharedPreferences.edit().clear().apply()
    }
}

// Create the AuthApi interface
interface AuthApi {
    suspend fun login(username: String, password: String): User
    suspend fun register(username: String, password: String): User
}

// Implementation of AuthRepository
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