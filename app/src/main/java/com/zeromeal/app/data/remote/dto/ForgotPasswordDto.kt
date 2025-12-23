package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

data class VerifyCodeRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("code")
    val code: String
)

data class ResetPasswordRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)
