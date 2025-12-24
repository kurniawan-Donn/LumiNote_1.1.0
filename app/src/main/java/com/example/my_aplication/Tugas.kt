package com.example.my_aplication  // Mendeklarasikan package/namespace untuk aplikasi

import java.util.UUID  // Import kelas UUID untuk membuat ID unik

// ===============================
// DATA CLASS TUGAS
// Mendefinisikan struktur data untuk entitas Tugas
// ===============================
data class Tugas(
    // ===============================
    // PROPERTIES (PROPERTI)
    // Mendefinisikan properti-properti dari data class Tugas
    // ===============================

    val id: String = UUID.randomUUID().toString(),  // ID unik yang di-generate otomatis menggunakan UUID
    // Nilai default: string UUID acak
    // Digunakan sebagai identifier unik setiap tugas

    val judul: String,  // Judul tugas (wajib diisi, tidak ada nilai default)
    // Properti utama yang merepresentasikan nama/inti tugas

    val deskripsi: String = "",  // Deskripsi detail tugas
    // Nilai default: string kosong
    // Opsional untuk diisi

    val tanggal: String? = null,  // Tanggal deadline/tenggat waktu tugas
    // Tipe nullable String? karena bisa tidak diisi
    // Format: biasanya "DD/MM/YYYY"

    val waktu: String? = null,  // Waktu deadline/tenggat waktu tugas
    // Tipe nullable String? karena bisa tidak diisi
    // Format: biasanya "HH:MM"

    val isSelesai: Boolean = false,  // Status penyelesaian tugas
    // Nilai default: false (belum selesai)
    // true = tugas sudah selesai

    val timestamp: Long = System.currentTimeMillis()  // Waktu pembuatan tugas dalam milidetik
    // Nilai default: waktu saat objek dibuat
    // Digunakan untuk sorting/ordering

) {
    // ===============================
    // METHOD/FUNGSI TAMBAHAN
    // Mendefinisikan fungsi-fungsi helper untuk class Tugas
    // ===============================

    // Fungsi untuk mengecek status tugas
    // Alias/convenience function untuk isSelesai
    fun isChecked(): Boolean = isSelesai
    // Mengembalikan nilai isSelesai langsung
    // Nama "isChecked" mungkin digunakan untuk kompatibilitas dengan UI checkbox

    // ===============================
    // FUNGSI PENCARIAN
    // Mengecek apakah tugas cocok dengan kata kunci pencarian
    // ===============================
    fun matchesQuery(katakunci: String): Boolean {
        val kata = katakunci.lowercase()  // Mengubah kata kunci menjadi huruf kecil untuk pencarian case-insensitive

        val judulLower = judul.lowercase()  // Mengubah judul menjadi huruf kecil
        val deskripsiLower = deskripsi.lowercase()  // Mengubah deskripsi menjadi huruf kecil

        val cocokDiJudul = judulLower.contains(kata)  // Mengecek apakah kata kunci ada dalam judul
        val cocokDiDeskripsi = deskripsiLower.contains(kata)  // Mengecek apakah kata kunci ada dalam deskripsi

        return cocokDiJudul || cocokDiDeskripsi  // Mengembalikan true jika cocok di judul ATAU deskripsi
    }
}