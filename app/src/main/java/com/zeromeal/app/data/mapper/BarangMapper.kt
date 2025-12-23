package com.zeromeal.app.data.mapper

import com.zeromeal.app.data.remote.dto.BarangDto
import com.zeromeal.app.domain.model.Barang

/**
 * Mapper untuk konversi BarangDto ke domain model Barang
 */
fun BarangDto.toDomain(): Barang {
    return Barang(
        id = barangId,
        namaBarang = namaBarang,
        satuanStandar = satuanStandar,
        kategori = kategori
    )
}

fun List<BarangDto>.toDomainList(): List<Barang> {
    return map { it.toDomain() }
}
