package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO untuk tabel daftar_belanja dari MySQL
 * Menyimpan daftar belanja user
 */
data class DaftarBelanjaDto(
    @SerializedName("belanja_id")
    val belanjaId: Int? = null,
    
    @SerializedName("user_id")
    val userId: Int,
    
    @SerializedName("nama_produk")
    val namaProduk: String,
    
    @SerializedName("jumlah_produk")
    val jumlahProduk: Int,
    
    @SerializedName("status_beli")
    val statusBeli: Boolean = false
)
