package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Wrapper untuk semua API response dari Laravel
 * Format: { "status": true, "message": "...", "data": [...] }
 */
data class ApiResponse<T>(
    @SerializedName("status")
    val status: Boolean,
    
    @SerializedName("data")
    val data: T?,
    
    @SerializedName("message")
    val message: String? = null
)
