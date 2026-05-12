package com.hastakala.shop.utils

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLoggedInUser(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getLoggedInUserId(): Int? {
        val value = prefs.getInt(KEY_USER_ID, -1)
        return value.takeIf { it != -1 }
    }

    fun clearSession() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    companion object {
        private const val PREFS_NAME = "hasta_kala_session"
        private const val KEY_USER_ID = "logged_in_user_id"
    }
}
