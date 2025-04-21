package com.example.audioemotionanalyzer.ui.progress

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.audioemotionanalyzer.R
import com.example.audioemotionanalyzer.databinding.FragmentProgressBinding
import kotlin.random.Random

class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Имитация процесса обработки аудио
        // В реальном приложении здесь был бы запрос к API
        simulateProcessing()
    }

    private fun simulateProcessing() {
        // Задержка в 3 секунды для имитации обработки
        handler.postDelayed({
            // Генерируем случайный результат для демонстрации
            // В реальном приложении здесь будет результат от API
            val emotionCode = Random.nextInt(0, 5) // От 0 до 4 включительно

            // Replace the ProgressFragmentDirections with a Bundle
            val bundle = Bundle().apply {
                putInt("emotionCode", emotionCode)
            }
            findNavController().navigate(R.id.action_progressFragment_to_resultFragment, bundle)
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}