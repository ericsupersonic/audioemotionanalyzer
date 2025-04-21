package com.example.audioemotionanalyzer.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.audioemotionanalyzer.R
import com.example.audioemotionanalyzer.databinding.FragmentSignupBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка кнопки Log in для создания аккаунта
        binding.btnLogin.setOnClickListener {
            val login = binding.etLogin.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val repeatPassword = binding.etRepeatPassword.text.toString().trim()

            if (validateInput(login, password, repeatPassword)) {
                // Здесь будет логика создания аккаунта через API
                // Для демонстрации просто переходим на экран загрузки аудио
                findNavController().navigate(R.id.action_signUpFragment_to_audioUploadFragment)
            }
        }
    }

    private fun validateInput(login: String, password: String, repeatPassword: String): Boolean {
        var isValid = true

        // Проверка логина
        if (login.isEmpty()) {
            binding.tilLogin.error = "Login cannot be empty"
            isValid = false
        } else if (!login.matches(Regex("^[a-zA-Z0-9]+$"))) {
            binding.tilLogin.error = "Use only latin alphabet and numbers"
            isValid = false
        } else {
            binding.tilLogin.error = null
        }

        // Проверка пароля
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password cannot be empty"
            isValid = false
        } else if (!password.matches(Regex("^[a-zA-Z0-9]+$"))) {
            binding.tilPassword.error = "Use only latin alphabet and numbers"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        // Проверка повторения пароля
        if (repeatPassword.isEmpty()) {
            binding.tilRepeatPassword.error = "Please repeat your password"
            isValid = false
        } else if (password != repeatPassword) {
            binding.tilRepeatPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.tilRepeatPassword.error = null
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}