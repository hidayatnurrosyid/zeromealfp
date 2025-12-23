package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO untuk tabel users dari MySQL
 */
data class UserDto(
    @SerializedName("user_id")
    val userId: Int,
    
    @SerializedName("nama")
    val nama: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String? = null, // Tidak dikembalikan dari API untuk keamanan
    
    @SerializedName("no_telepon")
    val noTelepon: String? = null
)

/**
 * Request untuk login
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String
)

/**
 * Response dari login
 */
data class LoginResponse(
    @SerializedName("user")
    val user: UserDto,
    
    @SerializedName("token")
    val token: String? = null
)
