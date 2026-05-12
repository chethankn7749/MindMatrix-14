package com.hastakala.shop.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.hastakala.shop.R
import com.hastakala.shop.activities.MainActivity
import com.hastakala.shop.databinding.ActivitySignInBinding
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.utils.LocalizedActivity
import com.hastakala.shop.viewmodels.AuthViewModel
import com.hastakala.shop.viewmodels.AuthViewModelFactory
import com.google.android.material.textfield.TextInputLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SignInActivity : LocalizedActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val viewModel: AuthViewModel by viewModels {
        val app = application as HastaKalaApplication
        AuthViewModelFactory(app.userRepository, app.sessionManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("prefill_email")?.let {
            binding.emailInput.editText?.setText(it)
        }
        intent.getStringExtra("signup_success")?.let {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        binding.createAccountButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(R.anim.nav_enter, R.anim.nav_exit)
        }

        binding.forgotPasswordText.setOnClickListener {
            val email = binding.emailInput.editText?.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInput.error = getString(com.hastakala.shop.R.string.enter_valid_email)
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val user = (application as HastaKalaApplication).userRepository.getUserByEmail(email.lowercase())
                if (user != null) {
                    showResetPasswordDialog(email.lowercase())
                } else {
                    Toast.makeText(this@SignInActivity, R.string.no_account_for_email, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.signInButton.setOnClickListener {
            val email = binding.emailInput.editText?.text.toString().trim()
            val password = binding.passwordInput.editText?.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInput.error = getString(com.hastakala.shop.R.string.enter_valid_email)
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.passwordInput.error = getString(com.hastakala.shop.R.string.password_length_error)
                return@setOnClickListener
            }
            binding.emailInput.error = null
            binding.passwordInput.error = null
            setLoading(true)
            viewModel.signIn(email, password)
        }

        viewModel.authState.observe(this) { result ->
            setLoading(false)
            result.onSuccess {
                Toast.makeText(this, R.string.welcome_back, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.nav_enter, R.anim.nav_exit)
                finish()
            }.onFailure {
                Toast.makeText(this, localizedAuthError(it.message, R.string.sign_in_failed), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun localizedAuthError(message: String?, fallback: Int): String = when (message) {
        "No account found for this email." -> getString(R.string.no_account_for_email)
        "Invalid email or password." -> getString(R.string.invalid_email_or_password)
        else -> message ?: getString(fallback)
    }

    private fun showResetPasswordDialog(email: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reset_password, null)
        val passwordInput = dialogView.findViewById<TextInputLayout>(R.id.resetPasswordInput)
        val confirmInput = dialogView.findViewById<TextInputLayout>(R.id.resetConfirmPasswordInput)
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.reset_password)
            .setMessage(getString(R.string.reset_password_message, email))
            .setView(dialogView)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val newPassword = passwordInput.editText?.text.toString()
                val confirmPassword = confirmInput.editText?.text.toString()
                passwordInput.error = null
                confirmInput.error = null
                when {
                    !isStrongPassword(newPassword) -> passwordInput.error = getString(R.string.password_strength_error)
                    newPassword != confirmPassword -> confirmInput.error = getString(R.string.password_mismatch)
                    else -> lifecycleScope.launch {
                        val updated = (application as HastaKalaApplication).userRepository.updatePassword(email, newPassword)
                        Toast.makeText(
                            this@SignInActivity,
                            if (updated) R.string.password_reset_success else R.string.no_account_for_email,
                            Toast.LENGTH_LONG
                        ).show()
                        if (updated) {
                            binding.passwordInput.editText?.setText("")
                            dialog.dismiss()
                        }
                    }
                }
            }
        }
        dialog.show()
    }

    private fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 &&
            password.any { it.isUpperCase() } &&
            password.any { it.isLowerCase() } &&
            password.any { it.isDigit() } &&
            password.any { !it.isLetterOrDigit() } &&
            password.none { it.isWhitespace() }
    }

    private fun setLoading(loading: Boolean) {
        binding.signInButton.isEnabled = !loading
        binding.createAccountButton.isEnabled = !loading
        binding.forgotPasswordText.isEnabled = !loading
        binding.signInButton.text = if (loading) getString(R.string.signing_in) else getString(com.hastakala.shop.R.string.sign_in)
    }
}
