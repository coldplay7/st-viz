package com.stviz.app.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val username: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class LoginScreenModel(private val repository: AuthRepository = AuthRepository()) : ScreenModel {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        screenModelScope.launch {
            _state.value = AuthState.Loading
            repository.login(LoginRequest(email, password))
                .onSuccess { 
                    _state.value = AuthState.Success(it.username)
                }
                .onFailure { 
                    _state.value = AuthState.Error(it.message ?: "Login failed")
                }
        }
    }
}
