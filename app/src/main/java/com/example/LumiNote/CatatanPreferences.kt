// Deklarasi package untuk organisasi kode
package com.example.LumiNote

// Mengimpor Context untuk mengakses resources dan sistem Android
import android.content.Context
// Mengimpor SharedPreferences untuk penyimpanan key-value sederhana
import android.content.SharedPreferences
// Mengimpor fungsi ekstensi edit() untuk mempermudah pengeditan SharedPreferences
import androidx.core.content.edit
// Mengimpor Gson untuk konversi objek Java/Kotlin ke JSON dan sebaliknya
import com.google.gson.Gson
// Mengimpor TypeToken untuk menentukan tipe data saat deserialisasi JSON
import com.google.gson.reflect.TypeToken
// Mengimpor Type untuk representasi tipe data generik
import java.lang.reflect.Type

// Kelas untuk mengelola penyimpanan data catatan menggunakan SharedPreferences
class CatatanPreferences(context: Context) {

    // Inisialisasi SharedPreferences dengan nama spesifik "catatan_prefs"
    // MODE_PRIVATE: Hanya aplikasi ini yang dapat mengakses preferences
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Inisialisasi Gson untuk serialisasi/deserialisasi JSON
    private val gson = Gson()

    // Companion object untuk menyimpan konstanta yang digunakan di seluruh kelas
    companion object {
        // Nama file SharedPreferences
        private const val PREFS_NAME = "catatan_prefs"
        // Key untuk menyimpan daftar catatan dalam SharedPreferences
        private const val KEY_CATATAN_LIST = "catatan_list"
    }

    // ===============================
    // Simpan semua catatan ke SharedPreferences
    // ===============================
    fun saveCatatanList(catatanList: List<Catatan>) {
        // Mengonversi List<Catatan> menjadi string JSON menggunakan Gson
        val json = gson.toJson(catatanList)
        // Menggunakan fungsi ekstensi edit() untuk mempermudah pengeditan SharedPreferences
        prefs.edit {
            // Menyimpan string JSON ke SharedPreferences dengan key KEY_CATATAN_LIST
            putString(KEY_CATATAN_LIST, json)
        }
    }

    // ===============================
    // Ambil semua catatan dari SharedPreferences
    // ===============================
    fun getCatatanList(): List<Catatan> {
        // Mengambil string JSON dari SharedPreferences
        // Jika null (tidak ada data), kembalikan list kosong
        val json = prefs.getString(KEY_CATATAN_LIST, null) ?: return emptyList()

        // Membuat TypeToken untuk menentukan tipe data List<Catatan> saat deserialisasi
        // TypeToken diperlukan karena Java/Kotlin memiliki type erasure saat runtime
        val type: Type = object : TypeToken<List<Catatan>>() {}.type

        // Mengonversi string JSON kembali menjadi List<Catatan>
        return gson.fromJson(json, type)
    }

    // ===============================
    // Tambah catatan baru ke dalam daftar
    // ===============================
    fun addCatatan(catatan: Catatan) {
        // Mengambil daftar catatan yang ada dan mengubahnya menjadi MutableList
        val currentList = getCatatanList().toMutableList()
        // Menambahkan catatan baru ke dalam list
        currentList.add(catatan)
        // Menyimpan list yang telah diperbarui
        saveCatatanList(currentList)
    }

    // ===============================
    // Update catatan yang sudah ada berdasarkan ID
    // ===============================
    fun updateCatatan(updatedCatatan: Catatan) {
        // Mengambil daftar catatan yang ada dan mengubahnya menjadi MutableList
        val list = getCatatanList().toMutableList()
        // Mencari index catatan berdasarkan ID yang cocok
        val index = list.indexOfFirst { it.id == updatedCatatan.id }

        // Jika catatan ditemukan (index != -1)
        if (index != -1) {
            // Mengganti catatan lama dengan catatan yang telah diupdate
            list[index] = updatedCatatan // update hanya catatan yang sama
        }
        // Menyimpan list yang telah diperbarui
        saveCatatanList(list)
    }

    // ===============================
    // Hapus catatan berdasarkan ID
    // ===============================
    fun deleteCatatan(id: String) {
        // Mengambil daftar catatan yang ada dan mengubahnya menjadi MutableList
        val currentList = getCatatanList().toMutableList()
        // Menghapus semua catatan dengan ID yang sesuai dari list
        currentList.removeAll { it.id == id }
        // Menyimpan list yang telah diperbarui
        saveCatatanList(currentList)
    }

    // ===============================
    // Ambil satu catatan berdasarkan ID
    // ===============================
    fun getCatatanById(id: String): Catatan? {
        // Menggunakan fungsi find untuk mencari catatan dengan ID yang sesuai
        // find: mengembalikan elemen pertama yang memenuhi kondisi, atau null jika tidak ditemukan
        return getCatatanList().find { it.id == id }
    }

    // ===============================
    // Catatan: Tidak ada implementasi clearAll() di sini
    // ===============================
    // Jika diperlukan, bisa ditambahkan:
    // fun clearAll() {
    //     prefs.edit { clear() }
    // }
}