package com.hastakala.shop.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import android.widget.Toast
import com.hastakala.shop.R
import com.hastakala.shop.databinding.ActivitySignUpBinding
import com.hastakala.shop.utils.HastaKalaApplication
import com.hastakala.shop.utils.LocalizedActivity
import com.hastakala.shop.viewmodels.AuthViewModel
import com.hastakala.shop.viewmodels.AuthViewModelFactory

class SignUpActivity : LocalizedActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: AuthViewModel by viewModels {
        val app = application as HastaKalaApplication
        AuthViewModelFactory(app.userRepository, app.sessionManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInText.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.nav_pop_enter, R.anim.nav_pop_exit)
        }

        binding.createAccountButton.setOnClickListener {
            val name = binding.nameInput.editText?.text.toString().trim()
            val email = binding.emailInput.editText?.text.toString().trim()
            val phone = binding.phoneInput.editText?.text.toString().trim()
            val phoneDigits = phone.filter { it.isDigit() }
            val password = binding.passwordInput.editText?.text.toString()
            val confirmPassword = binding.confirmPasswordInput.editText?.text.toString()

            when {
                name.isBlank() -> binding.nameInput.error = getString(com.hastakala.shop.R.string.required_field)
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.emailInput.error = getString(com.hastakala.shop.R.string.enter_valid_email)
                phoneDigits.length != 10 -> binding.phoneInput.error = getString(com.hastakala.shop.R.string.enter_valid_phone)
                !isStrongPassword(password) -> binding.passwordInput.error = getString(com.hastakala.shop.R.string.password_strength_error)
                password != confirmPassword -> binding.confirmPasswordInput.error = getString(com.hastakala.shop.R.string.password_mismatch)
                else -> {
                    clearErrors()
                    setLoading(true)
                    viewModel.signUp(name, email, phoneDigits, password)
                }
            }
        }

        viewModel.authState.observe(this) { result ->
            setLoading(false)
            result.onSuccess {
                viewModel.logout()
                startActivity(
                    Intent(this, SignInActivity::class.java)
                        .putExtra("prefill_email", binding.emailInput.editText?.text.toString().trim())
                        .putExtra("signup_success", getString(R.string.account_created_sign_in))
                )
                overridePendingTransition(R.anim.nav_enter, R.anim.nav_exit)
                finish()
            }.onFailure {
                Toast.makeText(this, localizedAuthError(it.message), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun localizedAuthError(message: String?): String = when (message) {
        "An account already exists with this email." -> getString(R.string.account_exists_for_email)
        else -> message ?: getString(R.string.unable_to_create_account)
    }

    private fun clearErrors() {
        binding.nameInput.error = null
        binding.emailInput.error = null
        binding.phoneInput.error = null
        binding.passwordInput.error = null
        binding.confirmPasswordInput.error = null
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
        binding.createAccountButton.isEnabled = !loading
        binding.signInText.isEnabled = !loading
        binding.createAccountButton.text = if (loading) getString(R.string.creating_account) else getString(com.hastakala.shop.R.string.create_account)
    }
}
