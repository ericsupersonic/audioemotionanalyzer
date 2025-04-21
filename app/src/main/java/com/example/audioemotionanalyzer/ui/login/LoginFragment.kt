package com.example.audioemotionanalyzer.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.audioemotionanalyzer.R
import com.example.audioemotionanalyzer.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка кнопки Log in
        binding.btnLogin.setOnClickListener {
            val login = binding.etLogin.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(login, password)) {
                // Здесь будет логика аутентификации через API
                // Для демонстрации просто переходим на экран загрузки аудио
                findNavController().navigate(R.id.action_loginFragment_to_audioUploadFragment)
            }
        }
    }

    private fun validateInput(login: String, password: String): Boolean {
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

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}