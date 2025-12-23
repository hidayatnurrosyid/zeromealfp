package com.zeromeal.app.ui.forgotpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zeromeal.app.data.remote.ApiService
import com.zeromeal.app.data.remote.dto.VerifyCodeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val email: String = savedStateHandle.get<String>("email") ?: ""

    private val _code = MutableStateFlow("")
    val code = _code.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _event = Channel<VerifyCodeEvent>()
    val event = _event.receiveAsFlow()

    fun onCodeChange(value: String) {
        if (value.length <= 6 && value.all { it.isDigit() }) {
            _code.value = value
            _errorMessage.value = null
        }
    }

    fun onSubmit() {
        val codeValue = _code.value.trim()

        if (codeValue.length != 6) {
            _errorMessage.value = "Kode harus 6 digit"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = apiService.verifyResetCode(VerifyCodeRequest(email, codeValue))

                if (response.isSuccessful && response.body()?.status == true) {
                    _event.send(VerifyCodeEvent.Success(email, codeValue))
                } else {
                    val errorMsg = response.body()?.message ?: "Kode tidak valid"
                    _errorMessage.value = errorMsg
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal terhubung ke server: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    sealed class VerifyCodeEvent {
        data class Success(val email: String, val code: String) : VerifyCodeEvent()
    }
}
