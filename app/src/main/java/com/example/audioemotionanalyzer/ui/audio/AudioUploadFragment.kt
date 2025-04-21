package com.example.audioemotionanalyzer.ui.audio

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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

class AudioUploadFragment : Fragment() {

    private var _binding: FragmentAudioUploadBinding? = null
    private val binding get() = _binding!!
    private var selectedAudioUri: Uri? = null
    private val TAG = "AudioUploadFragment"


    private val getAudio = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedAudioUri = uri
                Log.d(TAG, "Audio selected: $uri")


                val fileName = getFileName(uri)


                binding.tvDragHere.text = fileName
                binding.ivAudioIcon.setImageResource(R.drawable.ic_audio_selected)
            }
        }
    }

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

        binding.dragContainer.setOnClickListener {
            checkPermissionAndOpenPicker()
        }


        binding.btnStart.setOnClickListener {
            if (selectedAudioUri != null) {
                Log.d(TAG, "Start button clicked with URI: $selectedAudioUri")

                val bundle = Bundle()
                bundle.putString("audioUriString", selectedAudioUri.toString())
                findNavController().navigate(R.id.action_audioUploadFragment_to_progressFragment, bundle)
            } else {
                Toast.makeText(requireContext(), getString(R.string.select_audio_first), Toast.LENGTH_SHORT).show()
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