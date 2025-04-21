package com.example.audioemotionanalyzer.ui.audio

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.audioemotionanalyzer.R
import com.example.audioemotionanalyzer.databinding.FragmentAudioUploadBinding
import java.io.File

class AudioUploadFragment : Fragment() {

    private var _binding: FragmentAudioUploadBinding? = null
    private val binding get() = _binding!!
    private var selectedAudioUri: Uri? = null

    // Обработчик результата выбора аудио
    private val getAudio = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedAudioUri = uri

                // Получаем имя файла из URI
                val fileName = getFileName(uri)

                // Обновляем UI с именем файла
                binding.tvDragHere.text = fileName
                binding.ivAudioIcon.setImageResource(R.drawable.ic_audio_selected)
            }
        }
    }

    // Обработчик запроса разрешений
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            openAudioPicker()
        } else {
            Toast.makeText(requireContext(), "Permissions denied. Cannot select audio files.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка контейнера для перетаскивания
        binding.dragContainer.setOnClickListener {
            checkPermissionAndOpenPicker()
        }

        // Настройка кнопки Start
        binding.btnStart.setOnClickListener {
            if (selectedAudioUri != null) {
                // Здесь будет логика отправки аудио на сервер
                // Для демонстрации просто переходим на экран обработки
                findNavController().navigate(R.id.action_audioUploadFragment_to_progressFragment)
            } else {
                Toast.makeText(requireContext(), "Please select an audio file first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionAndOpenPicker() {
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        val allPermissionsGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            openAudioPicker()
        } else {
            requestPermissionLauncher.launch(requiredPermissions)
        }
    }

    private fun openAudioPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        getAudio.launch(intent)
    }

    // Функция для получения имени файла из URI
    private fun getFileName(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)

        return cursor?.use {
            val nameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            it.moveToFirst()
            if (nameIndex != -1 && !it.isNull(nameIndex)) {
                it.getString(nameIndex)
            } else {
                "Selected audio file"
            }
        } ?: "Selected audio file"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}