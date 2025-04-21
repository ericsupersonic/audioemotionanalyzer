
package com.example.audioemotionanalyzer.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)


    fun getAuthToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun saveUser(user: User) {
        sharedPreferences.edit() {
            putString("user_id", user.id)
                .putString("username", user.username)
                .putString("token", user.token)
        }
    }

    fun getUser(): User? {
        val id = sharedPreferences.getString("user_id", null) ?: return null
        val username = sharedPreferences.getString("username", null) ?: return null
        val token = sharedPreferences.getString("token", null) ?: return null

        return User(id, username, token)
    }

    fun clearAuth() {
        sharedPreferences.edit() { clear() }
    }
}