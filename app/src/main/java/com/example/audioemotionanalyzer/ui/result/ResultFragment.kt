package com.example.audioemotionanalyzer.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.audioemotionanalyzer.R
import com.example.audioemotionanalyzer.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val args: ResultFragmentArgs by navArgs()

    // Маппинг эмоций
    private val emotionMap = mapOf(
        0 to "neutral",
        1 to "angry",
        2 to "positive",
        3 to "sad",
        4 to "other"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем эмоцию из аргументов или используем значение по умолчанию
        val emotionCode = args.emotionCode
        val emotion = emotionMap[emotionCode] ?: "unknown"

        // Устанавливаем текст с результатом
        binding.tvResult.text = "This audio probably contains emotion of ${emotion}."

        val iconResId = getEmotionIconResource(emotionCode)
        binding.ivEmotionIcon.setImageResource(iconResId)


        binding.btnAnalyzeNew.setOnClickListener {

            findNavController().navigate(R.id.action_resultFragment_to_audioUploadFragment)
        }
    }


    private fun getEmotionIconResource(emotionCode: Int): Int {
        return when (emotionCode) {
            0 -> R.drawable.ic_neutral // Нейтральный
            1 -> R.drawable.ic_angry   // Злость
            2 -> R.drawable.ic_smile   // Позитивный/счастливый
            3 -> R.drawable.ic_sad     // Грусть
            4 -> R.drawable.ic_other   // Другое
            else -> R.drawable.ic_neutral
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}