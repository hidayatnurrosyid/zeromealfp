package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object untuk Resep dari API Laravel
 * Sesuai dengan tabel resep di MySQL
 */
data class ResepDto(
    @SerializedName("resep_id")
    val resepId: Int,
    
    @SerializedName("admin_id")
    val adminId: Int? = null,

    @SerializedName("judul")
    val judul: String,

    @SerializedName("deskripsi")
    val deskripsi: String,

    @SerializedName("difficulty")
    val difficulty: String, // Easy, Medium, Hard

    @SerializedName("image_url")
    val imageUrl: String?,
    
    @SerializedName("waktu_pembuatan_menit")
    val waktuPembuatanMenit: Int? = null,
    
    @SerializedName("kalori_per_porsi")
    val kaloriPerPorsi: Int? = null,
    
    @SerializedName("karbohidrat_gram")
    val karbohidratGram: Double? = null,
    
    @SerializedName("protein_gram")
    val proteinGram: Double? = null,
    
    @SerializedName("lemak_gram")
    val lemakGram: Double? = null,
    
    @SerializedName("fun_fact")
    val funFact: String? = null,

    @SerializedName("langkah")
    val langkah: String?,
    
    @SerializedName("rating")
    val rating: Double,
    
    // Daftar bahan dari detail_resep_bahan
    @SerializedName("bahan")
    val bahan: List<DetailResepBahanDto>? = null
)
