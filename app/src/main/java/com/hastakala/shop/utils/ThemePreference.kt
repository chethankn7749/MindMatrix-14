package com.hastakala.shop.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemePreference {
    private const val PREFS = "hasta_kala_prefs"
    private const val KEY_DARK = "dark_mode"

    fun applyTheme(context: Context) {
        val darkMode = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK, false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK, enabled)
            .apply()
        applyTheme(context)
    }

    fun isDarkMode(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_DARK, false)
}
