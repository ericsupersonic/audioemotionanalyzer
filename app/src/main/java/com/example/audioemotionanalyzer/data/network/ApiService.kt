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

// Data models
data class EmotionResponse(
    val emotionCode: Int,
    val emotionName: String,
    val confidence: Float
)

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String, val userId: String, val username: String)

data class RegisterRequest(val username: String, val password: String)
data class RegisterResponse(val token: String, val userId: String, val username: String)