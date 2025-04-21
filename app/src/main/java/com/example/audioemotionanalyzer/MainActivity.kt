package com.example.audioemotionanalyzer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.audioemotionanalyzer.data.auth.AuthApiImpl
import com.example.audioemotionanalyzer.data.auth.AuthRepositoryImpl
import com.example.audioemotionanalyzer.data.auth.PreferencesManager
import com.example.audioemotionanalyzer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authRepository: AuthRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Navigation Component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Initialize auth repository
        val preferencesManager = PreferencesManager(this)
        authRepository = AuthRepositoryImpl(AuthApiImpl(), preferencesManager)

        // Check if user is already logged in
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        // If user is already logged in, navigate directly to audio upload screen
        if (authRepository.isLoggedIn()) {
            // Use a post to ensure navigation happens after the fragment is fully loaded
            binding.root.post {
                navController.navigate(R.id.audioUploadFragment)
            }
        }
    }

    // Handle back button navigation
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Handle logout functionality if needed
    fun logout() {
        authRepository.logout()
        navController.navigate(R.id.welcomeFragment)
    }
}