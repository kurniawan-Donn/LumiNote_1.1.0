package com.example.my_aplication

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class CatatanPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "catatan_prefs"
        private const val KEY_CATATAN_LIST = "catatan_list"
    }

    // Simpan semua catatan
    fun saveCatatanList(catatanList: List<Catatan>) {
        val json = gson.toJson(catatanList)
        prefs.edit {
            putString(KEY_CATATAN_LIST, json)
        }
    }

    // Ambil semua catatan
    fun getCatatanList(): List<Catatan> {
        val json = prefs.getString(KEY_CATATAN_LIST, null) ?: return emptyList()
        val type: Type = object : TypeToken<List<Catatan>>() {}.type
        return gson.fromJson(json, type)
    }

    // Tambah catatan baru
    fun addCatatan(catatan: Catatan) {
        val currentList = getCatatanList().toMutableList()
        currentList.add(catatan)
        saveCatatanList(currentList)
    }

    // Update catatan
    fun updateCatatan(updatedCatatan: Catatan) {
        val list = getCatatanList().toMutableList()
        val index = list.indexOfFirst { it.id == updatedCatatan.id }
        if (index != -1) {
            list[index] = updatedCatatan // update hanya catatan yang sama
        }
        saveCatatanList(list)
    }

    // Hapus catatan
    fun deleteCatatan(id: String) {
        val currentList = getCatatanList().toMutableList()
        currentList.removeAll { it.id == id }
        saveCatatanList(currentList)
    }

    // Ambil catatan berdasarkan ID
    fun getCatatanById(id: String): Catatan? {
        return getCatatanList().find { it.id == id }
    }
    // Clear semua catatan (untuk testing/debugging)
}
