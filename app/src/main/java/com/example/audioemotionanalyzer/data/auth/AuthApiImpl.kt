package com.example.audioemotionanalyzer.data.auth

import com.example.audioemotionanalyzer.data.network.ApiService
import com.example.audioemotionanalyzer.data.network.LoginRequest
import com.example.audioemotionanalyzer.data.network.LoginResponse
import com.example.audioemotionanalyzer.data.network.RegisterRequest
import com.example.audioemotionanalyzer.data.network.RetrofitConfig

class AuthApiImpl(private val apiService: ApiService = RetrofitConfig.apiService) : AuthApi {
    override suspend fun login(username: String, password: String): User {
        val request = LoginRequest(username, password)
        val response = apiService.login(request)
        return User(
            id = response.userId,
            username = response.username,
            token = response.token
        )
    }

    override suspend fun register(username: String, password: String): User {
        val request = RegisterRequest(username, password)
        val response = apiService.register(request)
        return User(
            id = response.userId,
            username = response.username,
            token = response.token
        )
    }
}