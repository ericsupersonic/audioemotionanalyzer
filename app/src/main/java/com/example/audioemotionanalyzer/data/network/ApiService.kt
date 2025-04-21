package com.example.audioemotionanalyzer.data.network

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("audio/analyze")
    suspend fun analyzeAudio(
        @Part audioFile: MultipartBody.Part,
        @Header("Authorization") token: String
    ): EmotionResponse

    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse


    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse
}


data class EmotionResponse(
    val emotionCode: Int,            // Код эмоции (0-4)
    val emotionName: String,
    val confidence: Float            // Уверенность в определении (0.0-1.0)
)


data class LoginRequest(
    val username: String,
    val password: String
)


data class LoginResponse(
    val token: String,               // JWT токен
    val userId: String,              // ID пользователя
    val username: String
)

/**
 * Запрос на регистрацию
 */
data class RegisterRequest(
    val username: String,
    val password: String
)

/**
 * Ответ на успешную регистрацию
 */
data class RegisterResponse(
    val token: String,               // JWT токен
    val userId: String,              // ID пользователя
    val username: String
)