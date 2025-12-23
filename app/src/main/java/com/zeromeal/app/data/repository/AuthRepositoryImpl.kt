package com.zeromeal.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.zeromeal.app.data.remote.ApiService
import com.zeromeal.app.data.remote.dto.LoginRequest
import com.zeromeal.app.data.remote.dto.UserDto
import com.zeromeal.app.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : AuthRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val _currentUser = MutableStateFlow<UserDto?>(null)
    override val currentUser: Flow<UserDto?> = _currentUser.asStateFlow()

    init {
        // Load saved user from SharedPreferences
        loadSavedUser()
    }
    
    private fun loadSavedUser() {
        val userId = prefs.getInt("user_id", -1)
        val nama = prefs.getString("user_nama", null)
        val email = prefs.getString("user_email", null)
        val noTelepon = prefs.getString("user_no_telepon", null)
        
        if (userId != -1 && nama != null && email != null) {
            _currentUser.value = UserDto(
                userId = userId,
                nama = nama,
                email = email,
                noTelepon = noTelepon
            )
        }
    }

    override suspend fun login(email: String, password: String): Result<UserDto> {
        return try {
            val request = LoginRequest(email = email, password = password)
            android.util.Log.d("AuthRepository", "Login request: email=$email")
            
            val response = apiService.login(request)
            android.util.Log.d("AuthRepository", "Response code: ${response.code()}")
            android.util.Log.d("AuthRepository", "Response body: ${response.body()}")
            android.util.Log.d("AuthRepository", "Response error: ${response.errorBody()?.string()}")
            
            if (response.isSuccessful) {
                val body = response.body()
                android.util.Log.d("AuthRepository", "Body status: ${body?.status}")
                android.util.Log.d("AuthRepository", "Body data: ${body?.data}")
                
                if (body?.status == true) {
                    val loginResponse = body.data
                    val user = loginResponse?.user
                    
                    android.util.Log.d("AuthRepository", "User: $user")
                    
                    if (user != null) {
                        saveUser(user)
                        Result.success(user)
                    } else {
                        Result.failure(Exception("Data user tidak ditemukan"))
                    }
                } else {
                    val errorMessage = body?.message ?: "Login gagal: Email atau password salah"
                    Result.failure(Exception(errorMessage))
                }
            } else {
                Result.failure(Exception("Login gagal: Email atau password salah"))
            }
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Login error", e)
            Result.failure(Exception("Gagal terhubung ke server: ${e.message}"))
        }
    }

    override suspend fun loginWithGoogle(): Boolean {
        // TODO: Implement Google login with backend
        return false
    }

    override suspend fun logout() {
        prefs.edit().clear().apply()
        _currentUser.value = null
    }

    override fun isLoggedIn(): Boolean {
        return prefs.getInt("user_id", -1) != -1
    }
    
    override fun getLoggedInUserId(): Int? {
        val userId = prefs.getInt("user_id", -1)
        return if (userId != -1) userId else null
    }

    private fun saveUser(user: UserDto) {
        prefs.edit()
            .putInt("user_id", user.userId)
            .putString("user_nama", user.nama)
            .putString("user_email", user.email)
            .putString("user_no_telepon", user.noTelepon)
            .apply()
        _currentUser.value = user
    }
}
