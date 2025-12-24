// Deklarasi package untuk organisasi kode
package com.example.my_aplication

// Mengimpor Context untuk mengakses resources dan sistem Android
import android.content.Context
// Mengimpor SharedPreferences untuk penyimpanan key-value sederhana
import android.content.SharedPreferences
// Mengimpor Gson untuk konversi objek Java/Kotlin ke JSON dan sebaliknya
import com.google.gson.Gson
// Mengimpor TypeToken untuk menentukan tipe data saat deserialisasi JSON
import com.google.gson.reflect.TypeToken

// Deklarasi kelas TugasPreferences untuk mengelola penyimpanan data tugas
class TugasPreferences(context: Context) {

    // ===============================
    // Inisialisasi SharedPreferences
    // ===============================
    // Membuat instance SharedPreferences dengan nama "tugas_prefs"
    // MODE_PRIVATE: Hanya aplikasi ini yang dapat mengakses preferences
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tugas_prefs", Context.MODE_PRIVATE)

    // ===============================
    // Inisialisasi Gson untuk serialisasi/deserialisasi
    // ===============================
    private val gson = Gson()  // Objek Gson untuk konversi JSON

    // ===============================
    // Companion Object untuk konstanta
    // ===============================
    companion object {
        // Key untuk menyimpan daftar tugas dalam SharedPreferences
        private const val KEY_TUGAS_LIST = "key_tugas_list"
    }

    // ===============================
    // Ambil semua tugas
    // ===============================
    fun getAllTugas(): List<Tugas> {
        // Mengambil string JSON dari SharedPreferences dengan key KEY_TUGAS_LIST
        // Jika tidak ada data (null), kembalikan list kosong
        val json = prefs.getString(KEY_TUGAS_LIST, null) ?: return emptyList()

        // Membuat TypeToken untuk menentukan tipe data List<Tugas> saat deserialisasi
        // TypeToken diperlukan karena Java/Kotlin memiliki type erasure saat runtime
        val type = object : TypeToken<List<Tugas>>() {}.type

        // Mengonversi string JSON kembali menjadi List<Tugas> menggunakan Gson
        return gson.fromJson(json, type)
    }

    // ===============================
    // Tambah tugas baru
    // ===============================
    fun addTugas(tugas: Tugas) {
        // Mengambil semua tugas yang ada dan mengubahnya menjadi MutableList
        val list = getAllTugas().toMutableList()

        // Menambahkan tugas baru ke dalam list
        list.add(tugas)

        // Menyimpan list yang telah diperbarui
        saveList(list)
    }

    // ===============================
    // Update tugas
    // ===============================
    fun updateTugas(tugas: Tugas) {
        // Mengambil semua tugas yang ada dan mengubahnya menjadi MutableList
        val list = getAllTugas().toMutableList()

        // Mencari index tugas berdasarkan ID yang cocok
        // indexOfFirst: mencari elemen pertama yang memenuhi kondisi
        val index = list.indexOfFirst { it.id == tugas.id }

        // Jika tugas ditemukan (index != -1)
        if (index != -1) {
            // Mengganti tugas lama dengan tugas yang baru (yang telah diupdate)
            list[index] = tugas

            // Menyimpan list yang telah diperbarui
            saveList(list)
        }
        // Jika tidak ditemukan, tidak melakukan apa-apa
    }

    // ===============================
    // Hapus tugas berdasarkan ID
    // ===============================
    fun deleteTugas(id: String) {
        // Mengambil semua tugas yang ada dan mengubahnya menjadi MutableList
        val list = getAllTugas().toMutableList()

        // Menghapus semua tugas dengan ID yang sesuai
        // removeAll: menghapus semua elemen yang memenuhi kondisi
        list.removeAll { it.id == id }

        // Menyimpan list yang telah diperbarui
        saveList(list)
    }

    // ===============================
    // Ambil satu tugas berdasarkan ID
    // ===============================
    fun getTugasById(id: String): Tugas? {
        // Menggunakan fungsi find untuk mencari tugas dengan ID yang sesuai
        // find: mengembalikan elemen pertama yang memenuhi kondisi, atau null jika tidak ditemukan
        return getAllTugas().find { it.id == id }
    }

    // ===============================
    // Simpan list ke SharedPreferences (private method)
    // ===============================
    private fun saveList(list: List<Tugas>) {
        // Mengonversi List<Tugas> menjadi string JSON menggunakan Gson
        val json = gson.toJson(list)

        // Membuka editor untuk mengubah SharedPreferences
        // apply(): menyimpan perubahan secara asynchronous (lebih cepat dari commit())
        prefs.edit().putString(KEY_TUGAS_LIST, json).apply()
    }
}