package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO untuk tabel detail_resep_bahan dari MySQL
 * Berisi bahan-bahan yang dibutuhkan untuk sebuah resep
 */
data class DetailResepBahanDto(
    @SerializedName("detail_id")
    val detailId: Int,
    
    @SerializedName("resep_id")
    val resepId: Int,
    
    @SerializedName("barang_id")
    val barangId: Int,
    
    @SerializedName("jumlah_dibutuhkan")
    val jumlahDibutuhkan: Double,
    
    @SerializedName("satuan")
    val satuan: String,
    
    // Optional: nama barang dari relasi
    @SerializedName("nama_barang")
    val namaBarang: String? = null
)
