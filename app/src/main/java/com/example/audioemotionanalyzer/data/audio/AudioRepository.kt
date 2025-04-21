package com.example.audioemotionanalyzer.data.audio

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.audioemotionanalyzer.data.auth.AuthRepository
import com.example.audioemotionanalyzer.data.auth.Result
import com.example.audioemotionanalyzer.data.network.ApiService
import com.example.audioemotionanalyzer.data.network.EmotionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

interface AudioRepository {
    suspend fun uploadAndAnalyzeAudio(audioUri: Uri): Result<EmotionResponse>
    suspend fun getAudioFile(uri: Uri): File
}

class AudioRepositoryImpl(
    private val apiService: ApiService,
    private val authRepository: AuthRepository,
    private val context: Context
) : AudioRepository {

    private val TAG = "AudioRepository"

    override suspend fun uploadAndAnalyzeAudio(audioUri: Uri): Result<EmotionResponse> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting audio upload and analysis for URI: $audioUri")


                val token = authRepository.getCurrentUser()?.token
                    ?: return@withContext Result.Error(Exception("Not authenticated"))

                Log.d(TAG, "User authenticated with token")


                val file = getAudioFile(audioUri)
                Log.d(TAG, "Audio file created: ${file.absolutePath}, size: ${file.length()} bytes")


                val requestFile = file.asRequestBody("audio/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("audio", file.name, requestFile)

                Log.d(TAG, "Sending API request to analyze audio")


                val response = apiService.analyzeAudio(body, "Bearer $token")
                Log.d(TAG, "API response received: emotionCode=${response.emotionCode}")

                Result.Success(response)
            } catch (e: Exception) {
                Log.e(TAG, "Error during audio analysis: ${e.message}", e)
                Result.Error(e)
            }
        }
    }

    override suspend fun getAudioFile(uri: Uri): File = withContext(Dispatchers.IO) {
        var inputStream: InputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(uri)
                ?: throw IOException("Cannot open input stream for URI: $uri")

            // Получаем расширение файла из URI или используем по умолчанию .m4a
            val fileName = getFileName(uri) ?:
            "audio_${System.currentTimeMillis()}${getFileExtension(uri)}"

            val tempFile = File(context.cacheDir, fileName)


            FileOutputStream(tempFile).use { outputStream ->
                val buffer = ByteArray(4 * 1024) // 4KB буфер
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }


            if (!tempFile.exists() || tempFile.length() == 0L) {
                throw Exception("Failed to create temporary audio file")
            }

            Log.d(TAG, "Audio file created successfully: ${tempFile.absolutePath}, size: ${tempFile.length()} bytes")
            tempFile
        } catch (e: Exception) {
            Log.e(TAG, "Error creating audio file: ${e.message}", e)
            throw e
        } finally {
            try {
                inputStream?.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing input stream: ${e.message}")
            }
        }
    }


    private fun getFileName(uri: Uri): String? {
        val contentResolver = context.contentResolver


        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    return cursor.getString(displayNameIndex)
                }
            }
        }

        // Если не удалось получить имя через DISPLAY_NAME, пробуем другой способ
        val path = uri.path
        if (path != null) {
            val cut = path.lastIndexOf('/')
            if (cut != -1) {
                return path.substring(cut + 1)
            }
        }

        return null
    }


    private fun getFileExtension(uri: Uri): String {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri)

        return when {
            mimeType?.contains("audio/mpeg") == true -> ".mp3"
            mimeType?.contains("audio/mp4") == true -> ".m4a"
            mimeType?.contains("audio/x-m4a") == true -> ".m4a"
            mimeType?.contains("audio/wav") == true -> ".wav"
            else -> ".m4a" // По умолчанию
        }
    }
}