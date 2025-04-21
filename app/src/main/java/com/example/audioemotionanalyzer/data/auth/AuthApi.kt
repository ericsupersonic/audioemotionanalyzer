package com.example.audioemotionanalyzer.data.auth

interface AuthApi {
    suspend fun login(username: String, password: String): User
    suspend fun register(username: String, password: String): User
}