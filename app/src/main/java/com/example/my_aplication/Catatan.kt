// Deklarasi package untuk organisasi kode
package com.example.my_aplication

// Mengimpor UUID untuk menghasilkan identifier unik
import java.util.UUID

// Deklarasi data class Catatan yang merepresentasikan struktur data catatan
// Data class otomatis mendapatkan fungsi equals(), hashCode(), toString(), copy()
data class Catatan(
    // ID unik yang di-generate otomatis menggunakan UUID jika tidak disediakan
    val id: String = UUID.randomUUID().toString(),

    // Judul catatan (wajib diisi, tidak boleh null)
    val judul: String,

    // Deskripsi singkat catatan (wajib diisi, tidak boleh null)
    val deskripsi: String,

    // Isi lengkap catatan (opsional, bisa null)
    val note: String? = null,

    // Tanggal catatan dalam format string (opsional, bisa null)
    val tanggal: String? = null,

    // Waktu catatan dalam format string (opsional, bisa null)
    val waktu: String? = null,

    // Timestamp untuk sorting/mengetahui kapan catatan dibuat/diubah
    val timestamp: Long = System.currentTimeMillis()
) {
    // ===============================
    // Helper function untuk mendapatkan tanggal & waktu dalam format display
    // ===============================
    fun getFormatTanggal(): String {
        // Jika ada tanggal dan waktu, tampilkan keduanya dengan baris baru
        if (tanggal != null && waktu != null) {
            return "$tanggal\n$waktu"
        }

        // Jika hanya tanggal yang ada, tampilkan tanggal saja
        if (tanggal != null) {
            return tanggal
        }

        // Jika hanya waktu yang ada, tampilkan waktu saja
        if (waktu != null) {
            return waktu
        }

        // Jika tidak ada tanggal dan waktu, kembalikan string kosong
        return ""
    }

    // ===============================
    // Helper function untuk search/filter
    // ===============================
    fun querypencocokan(query: String): Boolean {
        // Ubah query menjadi huruf kecil untuk pencarian yang tidak case-sensitive
        val searchText = query.lowercase()

        // Ubah judul menjadi huruf kecil untuk perbandingan
        val titleText = judul.lowercase()

        // Ubah deskripsi menjadi huruf kecil untuk perbandingan
        val descriptionText = deskripsi.lowercase()

        // Cek apakah query ada dalam judul
        val containsInTitle = titleText.contains(searchText)

        // Cek apakah query ada dalam deskripsi
        val containsInDescription = descriptionText.contains(searchText)

        // Kembalikan true jika query ditemukan di judul ATAU deskripsi
        return containsInTitle || containsInDescription
    }
}