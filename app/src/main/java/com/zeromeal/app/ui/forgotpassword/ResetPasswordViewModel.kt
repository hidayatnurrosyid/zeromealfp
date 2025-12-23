package com.zeromeal.app.ui.forgotpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.remote.ApiService
import com.zeromeal.app.data.remote.dto.ResetPasswordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val email: String = savedStateHandle.get<String>("email") ?: ""
    val code: String = savedStateHandle.get<String>("code") ?: ""

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _event = Channel<ResetPasswordEvent>()
    val event = _event.receiveAsFlow()

    fun onPasswordChange(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
        _errorMessage.value = null
    }

    fun onSubmit() {
        val passwordValue = _password.value
        val confirmPasswordValue = _confirmPassword.value

        if (passwordValue.isBlank()) {
            _errorMessage.value = "Password tidak boleh kosong"
            return
        }

        if (passwordValue.length < 6) {
            _errorMessage.value = "Password minimal 6 karakter"
            return
        }

        if (passwordValue != confirmPasswordValue) {
            _errorMessage.value = "Password tidak cocok"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val request = ResetPasswordRequest(
                    email = email,
                    code = code,
                    password = passwordValue,
                    passwordConfirmation = confirmPasswordValue
                )
                val response = apiService.resetPassword(request)

                if (response.isSuccessful && response.body()?.status == true) {
                    _event.send(ResetPasswordEvent.Success)
                } else {
                    val errorMsg = response.body()?.message ?: "Gagal reset password"
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal terhubung ke server: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    sealed class ResetPasswordEvent {
        object Success : ResetPasswordEvent()
    }
}
