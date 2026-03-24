package com.stviz.app.network

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class TokenManager(private val settings: Settings = Settings()) {
    companion object {
        private const val KEY_JWT_TOKEN = "jwt_token"
    }

    fun saveToken(token: String) {
        settings[KEY_JWT_TOKEN] = token
    }

    fun getToken(): String? {
        return settings.getStringOrNull(KEY_JWT_TOKEN)
    }

    fun clearToken() {
        settings.remove(KEY_JWT_TOKEN)
    }

    fun hasToken(): Boolean {
        return getToken() != null
    }
}
