package com.example.LumiNote

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("LumiNoteSession", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
    }

    // Simpan session login
    fun createLoginSession(idNama: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, idNama)
            apply()
        }
    }

    // Cek apakah user sudah login
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Mendapatkan ID user yang sedang login
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    // Logout - hapus session
    fun logout() {
        prefs.edit().clear().apply()
    }
}