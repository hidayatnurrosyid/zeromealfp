package com.zeromeal.app.domain.repository

import com.zeromeal.app.data.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface untuk autentikasi user dengan MySQL
 */
interface AuthRepository {
    val currentUser: Flow<UserDto?>
    suspend fun login(email: String, password: String): Result<UserDto>
    suspend fun loginWithGoogle(): Boolean
    suspend fun logout()
    fun isLoggedIn(): Boolean
    fun getLoggedInUserId(): Int?
}
