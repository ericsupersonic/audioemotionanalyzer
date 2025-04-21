package com.example.audioemotionanalyzer.ui.audio

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audioemotionanalyzer.data.audio.AudioRepository
import com.example.audioemotionanalyzer.data.auth.Result
import com.example.audioemotionanalyzer.data.network.EmotionResponse
import kotlinx.coroutines.launch

class AudioUploadViewModel(
    private val audioRepository: AudioRepository
) : ViewModel() {

    private val _uploadState = MutableLiveData<UploadState>()
    val uploadState: LiveData<UploadState> = _uploadState

    fun analyzeAudio(audioUri: Uri) {
        _uploadState.value = UploadState.Loading

        viewModelScope.launch {
            when (val result = audioRepository.uploadAndAnalyzeAudio(audioUri)) {
                is Result.Success -> _uploadState.value = UploadState.Success(result.data)
                is Result.Error -> _uploadState.value = UploadState.Error(result.exception.message ?: "Unknown error")
            }
        }
    }

    sealed class UploadState {
        object Loading : UploadState()
        data class Success(val response: EmotionResponse) : UploadState()
        data class Error(val message: String) : UploadState()
    }
}