package com.hastakala.shop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hastakala.shop.repositories.SalesRepository
import com.hastakala.shop.repositories.UserRepository
import com.hastakala.shop.utils.SessionManager

class AuthViewModelFactory(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(userRepository, sessionManager) as T
    }
}

class AppViewModelFactory(
    private val repository: SalesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(repository) as T
    }
}
