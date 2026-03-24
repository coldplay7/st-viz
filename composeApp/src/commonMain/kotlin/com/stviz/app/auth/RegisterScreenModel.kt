package com.stviz.app.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterScreenModel(private val repository: AuthRepository = AuthRepository()) : ScreenModel {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun register(username: String, email: String, password: String) {
        screenModelScope.launch {
            _state.value = AuthState.Loading
            repository.register(RegisterRequest(username, email, password))
                .onSuccess { 
                    _state.value = AuthState.Success(it.username)
                }
                .onFailure { 
                    _state.value = AuthState.Error(it.message ?: "Registration failed")
                }
        }
    }
}
