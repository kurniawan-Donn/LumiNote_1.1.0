package com.example.my_aplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CatatanViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = CatatanPreferences(application)

    private val _catatanList = MutableLiveData<List<Catatan>>()
    val catatanList: LiveData<List<Catatan>> = _catatanList

    private val _filteredCatatanList = MutableLiveData<List<Catatan>>()
    val filteredCatatanList: LiveData<List<Catatan>> = _filteredCatatanList

    init {
        loadCatatan()
    }

    // Load semua catatan dari storage
    fun loadCatatan() {
        val list = preferences.getCatatanList().sortedByDescending { it.timestamp }
        _catatanList.value = list
        _filteredCatatanList.value = list
    }

    // Tambah catatan baru
    fun addCatatan(catatan: Catatan) {
        preferences.addCatatan(catatan)
        loadCatatan()
    }

    // Update catatan
    fun updateCatatan(catatan: Catatan) {
        preferences.updateCatatan(catatan)
        loadCatatan()
    }

    // Hapus catatan
    fun deleteCatatan(id: String) {
        preferences.deleteCatatan(id)
        loadCatatan()
    }

    // Get catatan by ID
    fun getCatatanById(id: String): Catatan? {
        return preferences.getCatatanById(id)
    }

    // Filter/search catatan
    fun searchCatatan(query: String) {
        val allCatatan = _catatanList.value ?: emptyList()

        if (query.isEmpty()) {
            _filteredCatatanList.value = allCatatan
        } else {
            _filteredCatatanList.value = allCatatan.filter { it.matchesQuery(query) }
        }
    }

    // Reset filter
    fun resetFilter() {
        _filteredCatatanList.value = _catatanList.value
    }
}

