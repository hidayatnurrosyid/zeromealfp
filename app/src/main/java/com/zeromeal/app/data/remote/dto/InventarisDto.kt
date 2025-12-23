package com.zeromeal.app.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO untuk tabel inventaris_barang dari MySQL
 * Menyimpan stok barang yang dimiliki user dari Manual Input
 */
data class InventarisDto(
    @SerializedName("inventaris_id")
    val inventarisId: Int? = null,
    
    @SerializedName("user_id")
    val userId: Int,
    
    @SerializedName("barang_id")
    val barangId: Int? = null,
    
    @SerializedName("nama_barang_input")
    val namaBarangInput: String,
    
    @SerializedName("kategori_makanan")
    val kategoriMakanan: String? = null,
    
    @SerializedName("gambar")
    val gambar: String? = null,
    
    @SerializedName("jumlah")
    val jumlah: Int,
    
    @SerializedName("satuan_input")
    val satuanInput: String,
    
    @SerializedName("lokasi_penyimpanan")
    val lokasiPenyimpanan: String? = null,
    
    @SerializedName("catatan")
    val catatan: String? = null,
    
    @SerializedName("tanggal_pembelian")
    val tanggalPembelian: String,
    
    @SerializedName("tanggal_kadaluarsa")
    val tanggalKadaluarsa: String
)
