package com.zeromeal.app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _loginEvent = Channel<LoginEvent>()
    val loginEvent = _loginEvent.receiveAsFlow()

    fun onEmailChange(email: String) {
        _email.value = email
        _errorMessage.value = null // Clear error when typing
    }

    fun onPasswordChange(password: String) {
        _password.value = password
        _errorMessage.value = null // Clear error when typing
    }
    
    fun clearError() {
        _errorMessage.value = null
    }

    fun onLoginClick() {
        val emailValue = _email.value.trim()
        val passwordValue = _password.value
        
        // Validasi input
        if (emailValue.isBlank()) {
            _errorMessage.value = "Email tidak boleh kosong"
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _errorMessage.value = "Format email tidak valid"
            return
        }
        
        if (passwordValue.isBlank()) {
            _errorMessage.value = "Password tidak boleh kosong"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = authRepository.login(emailValue, passwordValue)
            
            _isLoading.value = false
            
            result.fold(
                onSuccess = { user ->
                    _loginEvent.send(LoginEvent.Success)
                },
                onFailure = { exception ->
                    _errorMessage.value = exception.message ?: "Login gagal"
                    _loginEvent.send(LoginEvent.Error(exception.message ?: "Login gagal"))
                }
            )
        }
    }

    fun onGoogleLoginClick() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            kotlinx.coroutines.delay(1000)
            val success = authRepository.loginWithGoogle()
            
            _isLoading.value = false
            
            if (success) {
                _loginEvent.send(LoginEvent.Success)
            } else {
                _errorMessage.value = "Google Login belum tersedia"
                _loginEvent.send(LoginEvent.Error("Google Login belum tersedia"))
            }
        }
    }

    sealed class LoginEvent {
        object Success : LoginEvent()
        data class Error(val message: String) : LoginEvent()
    }
}
