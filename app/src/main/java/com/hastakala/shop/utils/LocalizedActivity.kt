package com.hastakala.shop.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

open class LocalizedActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }
}
