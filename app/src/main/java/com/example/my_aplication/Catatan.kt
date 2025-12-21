package com.example.my_aplication

import java.util.UUID

/**
 * Data class untuk menyimpan satu catatan
 */
data class Catatan(
    val id: String = UUID.randomUUID().toString(),      // ID unik catatan
    val judul: String,                                  // Judul catatan
    val deskripsi: String,                              // Deskripsi singkat
    val note: String,                                   // Isi catatan
    val tanggal: String? = null,                        // Tanggal (opsional)
    val waktu: String? = null,                          // Waktu (opsional)
    val timestamp: Long = System.currentTimeMillis()    // Waktu dibuat/diupdate
) {

    /**
     * Menggabungkan tanggal dan waktu untuk tampilan
     */
    fun getDisplayDateTime(): String {
        return when {
            tanggal != null && waktu != null -> "$tanggal\n$waktu"
            tanggal != null -> tanggal
            waktu != null -> waktu
            else -> ""
        }
    }

    /**
     * Digunakan untuk fitur pencarian catatan
     */
    fun matchesQuery(query: String): Boolean {
        val keyword = query.lowercase()
        return judul.lowercase().contains(keyword) ||
                deskripsi.lowercase().contains(keyword) ||
                note.lowercase().contains(keyword)
    }
}
