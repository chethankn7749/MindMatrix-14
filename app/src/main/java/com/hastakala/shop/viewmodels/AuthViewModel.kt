package com.hastakala.shop.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hastakala.shop.models.UserEntity
import com.hastakala.shop.repositories.UserRepository
import com.hastakala.shop.utils.SessionManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableLiveData<Result<String>>()
    val authState: LiveData<Result<String>> = _authState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            runCatching {
                val user = userRepository.getUserByEmail(email.trim().lowercase())
                    ?: throw IllegalStateException("No account found for this email.")
                if (user.password != password) {
                    throw IllegalStateException("Invalid email or password.")
                }
                sessionManager.saveLoggedInUser(user.uid)
                "Welcome back"
            }.also { _authState.value = it }
        }
    }

    fun signUp(
        name: String,
        email: String,
        phone: String,
        password: String
    ) {
        viewModelScope.launch {
            runCatching {
                val normalizedEmail = email.trim().lowercase()
                if (userRepository.getUserByEmail(normalizedEmail) != null) {
                    throw IllegalStateException("An account already exists with this email.")
                }
                val userId = userRepository.saveUser(
                        UserEntity(
                            name = name,
                            email = normalizedEmail,
                            phoneNumber = phone,
                            password = password
                        )
                    ).toInt()
                sessionManager.saveLoggedInUser(userId)
                "Account created"
            }.also { _authState.value = it }
        }
    }

    fun currentUserId(): Int? = sessionManager.getLoggedInUserId()
    fun isLoggedIn(): Boolean = sessionManager.getLoggedInUserId() != null
    fun logout() = sessionManager.clearSession()
}
