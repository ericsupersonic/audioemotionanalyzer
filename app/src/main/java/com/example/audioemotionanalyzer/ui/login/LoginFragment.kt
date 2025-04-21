package com.example.audioemotionanalyzer.ui.login

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
import com.example.audioemotionanalyzer.data.auth.AuthApiImpl
import com.example.audioemotionanalyzer.data.auth.AuthRepositoryImpl
import com.example.audioemotionanalyzer.data.auth.PreferencesManager
import com.example.audioemotionanalyzer.data.auth.Result
import com.example.audioemotionanalyzer.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authRepository: AuthRepositoryImpl

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val preferencesManager = PreferencesManager(requireContext())
        val authApi = AuthApiImpl()
        authRepository = AuthRepositoryImpl(authApi, preferencesManager)


        binding.btnLogin.setOnClickListener {
            val login = binding.etLogin.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(login, password)) {
                performLogin(login, password)
            }
        }
    }

    private fun performLogin(login: String, password: String) {

        setLoadingState(true)

        lifecycleScope.launch {
            try {
                when (val result = authRepository.login(login, password)) {
                    is Result.Success -> {

                        findNavController().navigate(R.id.action_loginFragment_to_audioUploadFragment)
                    }
                    is Result.Error -> {
                        val errorMessage = when (val exception = result.exception) {
                            is IOException -> "Network error: Check your connection"
                            is HttpException -> "Server error: ${exception.code()}"
                            else -> "Unknown error: ${exception.localizedMessage}"
                        }
                        showErrorMessage(errorMessage)
                    }

                }
            } catch (e: Exception) {
                showErrorMessage("Connection failed: ${e.localizedMessage}")
                // Log the full exception for debugging
                Log.e("LoginFragment", "Login error", e)
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun validateInput(login: String, password: String): Boolean {
        var isValid = true

        // Валидация логина
        if (login.isEmpty()) {
            binding.tilLogin.error = getString(R.string.error_empty_login)
            isValid = false
        } else if (!login.matches(Regex("^[a-zA-Z0-9]+$"))) {
            binding.tilLogin.error = getString(R.string.error_login_format)
            isValid = false
        } else {
            binding.tilLogin.error = null
        }

        // Валидация пароля
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (!password.matches(Regex("^[a-zA-Z0-9]+$"))) {
            binding.tilPassword.error = getString(R.string.error_password_format)
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