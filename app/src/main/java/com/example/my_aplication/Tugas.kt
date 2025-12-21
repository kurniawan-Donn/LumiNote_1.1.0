package com.example.my_aplication

import java.util.UUID

data class Tugas(
    val id: String = UUID.randomUUID().toString(), // ID unik
    val judul: String,                              // tugas_judul
    val deskripsi: String = "",                     // tugas_deskripsi
    val tanggal: String? = null,                    //  tgl tugas
    val waktu: String? = null,                      // waktu deadline
    val isSelesai: Boolean = false,                 // CheckBox centang
    val timestamp: Long = System.currentTimeMillis() // waktu buat / update
) {

    // Untuk menampilkan status checklist
    fun isChecked(): Boolean = isSelesai

    // Helper untuk search
    fun matchesQuery(query: String): Boolean {
        val q = query.lowercase()
        return judul.lowercase().contains(q) ||
                deskripsi.lowercase().contains(q)
    }
}