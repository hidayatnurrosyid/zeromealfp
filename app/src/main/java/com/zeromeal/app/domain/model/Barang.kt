package com.zeromeal.app.domain.model

/**
 * Domain model untuk Barang
 */
data class Barang(
    val id: Int,
    val namaBarang: String,
    val satuanStandar: String,
    val kategori: String
)
