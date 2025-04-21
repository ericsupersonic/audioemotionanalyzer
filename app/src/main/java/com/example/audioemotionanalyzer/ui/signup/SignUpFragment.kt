package com.example.audioemotionanalyzer.ui.signup

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
import com.example.audioemotionanalyzer.databinding.FragmentSignupBinding
import kotlinx.coroutines.launch
import java.lang.SecurityException

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var authRepository: AuthRepositoryImpl

    companion object {
        private const val TAG = "SignUpFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
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
            val repeatPassword = binding.etRepeatPassword.text.toString().trim()

            if (validateInput(login, password, repeatPassword)) {
                performRegistration(login, password)
            }
        }
    }

    private fun performRegistration(login: String, password: String) {
        setLoadingState(true)

        lifecycleScope.launch {
            try {
                when (val result = authRepository.register(login, password)) {
                    is Result.Success -> {
                        findNavController().navigate(R.id.action_signUpFragment_to_welcomeFragment)
                        Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> {
                        Log.e(TAG, "Registration error: ${result.exception.message}", result.exception)

                        showErrorMessage(result.exception.message ?: "Registration failed")
                    }
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "Security exception during registration", e)
                showErrorMessage("Security error: ${e.message}")
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during registration", e)
                showErrorMessage("Registration error: ${e.localizedMessage}")
            } finally {
                // Hide loading state
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

    private fun validateInput(login: String, password: String, repeatPassword: String): Boolean {
        var isValid = true

        if (login.isEmpty()) {
            binding.tilLogin.error = getString(R.string.error_empty_login)
            isValid = false
        } else if (!login.matches(Regex("^[a-zA-Z0-9]+$"))) {
            binding.tilLogin.error = getString(R.string.error_login_format)
            isValid = false
        } else {
            binding.tilLogin.error = null
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (!password.matches(Regex("^[a-zA-Z0-9]+$"))) {
            binding.tilPassword.error = getString(R.string.error_password_format)
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (repeatPassword.isEmpty()) {
            binding.tilRepeatPassword.error = getString(R.string.error_repeat_password)
            isValid = false
        } else if (password != repeatPassword) {
            binding.tilRepeatPassword.error = getString(R.string.error_passwords_not_match)
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