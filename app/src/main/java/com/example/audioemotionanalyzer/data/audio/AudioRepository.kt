package com.example.audioemotionanalyzer.data.audio

import android.content.Context
import android.net.Uri
import com.example.audioemotionanalyzer.data.auth.AuthRepository
import com.example.audioemotionanalyzer.data.auth.Result
import com.example.audioemotionanalyzer.data.network.ApiService
import com.example.audioemotionanalyzer.data.network.EmotionResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

interface AudioRepository {
    suspend fun uploadAndAnalyzeAudio(audioUri: Uri): Result<EmotionResponse>
    suspend fun getAudioFile(uri: Uri): File
}

class AudioRepositoryImpl(
    private val apiService: ApiService,
    private val authRepository: AuthRepository,
    private val context: Context
) : AudioRepository {
    override suspend fun uploadAndAnalyzeAudio(audioUri: Uri): Result<EmotionResponse> {
        try {
            val file = getAudioFile(audioUri)
            val requestFile = file.asRequestBody("audio/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("audio", file.name, requestFile)

            val token = authRepository.getCurrentUser()?.token
                ?: return Result.Error(Exception("Not authenticated"))

            val response = apiService.analyzeAudio(body, "Bearer $token")
            return Result.Success(response)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun getAudioFile(uri: Uri): File {
        // Implementation to get the actual file from URI
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File(context.cacheDir, "temp_audio_${System.currentTimeMillis()}.mp3")

        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return tempFile
    }
}