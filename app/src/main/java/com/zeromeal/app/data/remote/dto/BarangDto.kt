package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object untuk Barang dari API Laravel
 * Nama field harus sama persis dengan response JSON dari server
 */
data class BarangDto(
    @SerializedName("barang_id")
    val barangId: Int,
    
    @SerializedName("nama_barang")
    val namaBarang: String,
    
    @SerializedName("satuan_standar")
    val satuanStandar: String,
    
    @SerializedName("kategori")
    val kategori: String
)
