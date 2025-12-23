package com.zeromeal.app.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.remote.ApiService
import com.zeromeal.app.data.remote.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _nama = MutableStateFlow("")
    val nama = _nama.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _noTelepon = MutableStateFlow("")
    val noTelepon = _noTelepon.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _registerEvent = Channel<RegisterEvent>()
    val registerEvent = _registerEvent.receiveAsFlow()

    fun onNamaChange(value: String) {
        _nama.value = value
        _errorMessage.value = null
    }

    fun onEmailChange(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun onConfirmPasswordChange(value: String) {
        _confirmPassword.value = value
        _errorMessage.value = null
    }

    fun onNoTeleponChange(value: String) {
        _noTelepon.value = value
        _errorMessage.value = null
    }

    fun onRegisterClick() {
        val namaValue = _nama.value.trim()
        val emailValue = _email.value.trim()
        val passwordValue = _password.value
        val confirmPasswordValue = _confirmPassword.value
        val noTeleponValue = _noTelepon.value.trim()

        // Validasi
        if (namaValue.isBlank()) {
            _errorMessage.value = "Nama tidak boleh kosong"
            return
        }

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
                val userDto = UserDto(
                    userId = 0,
                    nama = namaValue,
                    email = emailValue,
                    password = passwordValue,
                    noTelepon = noTeleponValue.ifBlank { null }
                )

                val response = apiService.register(userDto)

                if (response.isSuccessful && response.body()?.status == true) {
                    _registerEvent.send(RegisterEvent.Success)
                } else {
                    val errorMsg = response.body()?.message ?: "Pendaftaran gagal"
                    _errorMessage.value = errorMsg
                    _registerEvent.send(RegisterEvent.Error(errorMsg))
                }
            } catch (e: Exception) {
                val errorMsg = "Gagal terhubung ke server: ${e.message}"
                _errorMessage.value = errorMsg
                _registerEvent.send(RegisterEvent.Error(errorMsg))
            } finally {
                _isLoading.value = false
            }
        }
    }

    sealed class RegisterEvent {
        object Success : RegisterEvent()
        data class Error(val message: String) : RegisterEvent()
    }
}
