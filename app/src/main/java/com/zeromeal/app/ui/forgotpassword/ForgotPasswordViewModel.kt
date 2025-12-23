package com.zeromeal.app.ui.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.remote.ApiService
import com.zeromeal.app.data.remote.dto.ForgotPasswordRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _event = Channel<ForgotPasswordEvent>()
    val event = _event.receiveAsFlow()

    fun onEmailChange(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun onSubmit() {
        val emailValue = _email.value.trim()

        if (emailValue.isBlank()) {
            _errorMessage.value = "Email tidak boleh kosong"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            _errorMessage.value = "Format email tidak valid"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = apiService.forgotPassword(ForgotPasswordRequest(emailValue))

                if (response.isSuccessful && response.body()?.status == true) {
                    _event.send(ForgotPasswordEvent.Success(emailValue))
                } else {
                    val errorMsg = response.body()?.message ?: "Email tidak terdaftar"
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal terhubung ke server: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    sealed class ForgotPasswordEvent {
        data class Success(val email: String) : ForgotPasswordEvent()
    }
}
