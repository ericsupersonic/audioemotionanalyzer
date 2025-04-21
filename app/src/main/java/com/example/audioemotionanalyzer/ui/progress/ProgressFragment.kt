package com.example.audioemotionanalyzer.ui.progress

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.audioemotionanalyzer.R
import com.example.audioemotionanalyzer.data.audio.AudioRepositoryImpl
import com.example.audioemotionanalyzer.data.auth.AuthApiImpl
import com.example.audioemotionanalyzer.data.auth.AuthRepositoryImpl
import com.example.audioemotionanalyzer.data.auth.PreferencesManager
import com.example.audioemotionanalyzer.data.auth.Result
import com.example.audioemotionanalyzer.data.network.RetrofitConfig
import com.example.audioemotionanalyzer.databinding.FragmentProgressBinding
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!
    private lateinit var audioRepository: AudioRepositoryImpl
    private var audioUri: Uri? = null
    private val TAG = "ProgressFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем URI аудио из аргументов
        arguments?.getString("audioUriString")?.let { uriString ->
            Log.d(TAG, "Received URI string: $uriString")
            try {
                audioUri = uriString.toUri()
                Log.d(TAG, "Successfully parsed URI: $audioUri")
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing URI: ${e.message}")
            }
        }


        val preferencesManager = PreferencesManager(requireContext())
        val authRepository = AuthRepositoryImpl(AuthApiImpl(), preferencesManager)
        val apiService = RetrofitConfig.apiService
        audioRepository = AudioRepositoryImpl(apiService, authRepository, requireContext())

        // обработка аудиофайла
        processAudio()
    }

    private fun processAudio() {
        if (audioUri == null) {
            Log.e(TAG, "Audio URI is null, cannot process audio")
            Toast.makeText(requireContext(), "No audio file selected", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }


        binding.tvProgress.text = getString(R.string.in_progress)
        binding.progressBar.visibility = View.VISIBLE


        lifecycleScope.launch {
            try {
                Log.d(TAG, "Starting upload and analyze for URI: $audioUri")
                val result = audioRepository.uploadAndAnalyzeAudio(audioUri!!)

                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "Analysis success with emotion code: ${result.data.emotionCode}")
                        val bundle = Bundle().apply {
                            putInt("emotionCode", result.data.emotionCode)
                        }
                        findNavController().navigate(R.id.action_progressFragment_to_resultFragment, bundle)
                    }
                    is Result.Error -> {
                        val errorMsg = "Error analyzing audio: ${result.exception.message}"
                        Log.e(TAG, errorMsg, result.exception)
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                        findNavController().navigateUp()
                    }
                }
            } catch (e: Exception) {
                val errorMsg = "Error processing audio: ${e.message}"
                Log.e(TAG, errorMsg, e)
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}