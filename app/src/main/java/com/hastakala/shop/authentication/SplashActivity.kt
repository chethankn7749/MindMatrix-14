package com.hastakala.shop.authentication

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.hastakala.shop.R
import com.hastakala.shop.activities.MainActivity
import com.hastakala.shop.databinding.ActivitySplashBinding
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.utils.LocalizedActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : LocalizedActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as HastaKalaApplication
        lifecycleScope.launch {
            delay(1500)
            val destination = if (app.sessionManager.getLoggedInUserId() != null) {
                MainActivity::class.java
            } else {
                SignInActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, destination))
            overridePendingTransition(R.anim.nav_enter, R.anim.nav_exit)
            finish()
        }
    }
}
