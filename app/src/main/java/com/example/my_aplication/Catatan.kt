package com.example.my_aplication

import java.util.UUID

data class Catatan(
    val id: String = UUID.randomUUID().toString(),
    val judul: String,
    val deskripsi: String,
    val note: String? = null,
    val tanggal: String? = null,
    val waktu: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Helper function untuk mendapatkan tanggal & waktu dalam format display
    fun getFormatTanggal(): String {
        // Jika ada tanggal dan waktu
        if (tanggal != null && waktu != null) { return "$tanggal\n$waktu" }
        if (tanggal != null) { return tanggal }
        if (waktu != null) { return waktu }
        return ""
    }


    // Helper function untuk search/filter
    fun querypencocokan(query: String): Boolean {
        // Ubah query menjadi huruf kecil biar pencarian tidak case‑sensitive
        val searchText = query.lowercase()
        // Ubah judul dan deskripsi jadi huruf kecil juga
        val titleText = judul.lowercase()
        val descriptionText = deskripsi.lowercase()
        // Cek apakah judul berisi query
        val containsInTitle = titleText.contains(searchText)
        // Cek apakah deskripsi berisi query
        val containsInDescription = descriptionText.contains(searchText)
        // Kalau salah satu ada yang cocok, kembalikan true
        return containsInTitle || containsInDescription
    }

}