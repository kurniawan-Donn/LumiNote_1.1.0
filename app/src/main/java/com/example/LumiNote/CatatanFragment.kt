package com.example.LumiNote

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CatatanFragment : Fragment() {

    private lateinit var preferences: CatatanPreferences
    private lateinit var faforitPreferences: FaforitPreferences // ✅ TAMBAHAN
    private lateinit var adapter: CatatanAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private var allCatatan = listOf<Catatan>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catatan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = CatatanPreferences(requireContext())
        faforitPreferences = FaforitPreferences(requireContext()) // ✅ TAMBAHAN

        recyclerView = view.findViewById(R.id.rv_catatan)
        searchEditText = view.findViewById(R.id.et_search)

        setupRecyclerView()
        setupSearch()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = CatatanAdapter(
            catatanList = emptyList(),
            onItemClick = { catatan ->
                val intent = Intent(requireContext(), CatatanActivity::class.java).apply {
                    putExtra("id", catatan.id)
                    putExtra("judul", catatan.judul)
                    putExtra("deskripsi", catatan.deskripsi)
                    putExtra("tanggal", catatan.tanggal)
                    putExtra("waktu", catatan.waktu)
                }
                startActivity(intent)
            },
            onEditClick = { catatan ->
                val intent = Intent(requireContext(), TambahCatatanActivity::class.java).apply {
                    putExtra("id", catatan.id)
                    putExtra("judul", catatan.judul)
                    putExtra("deskripsi", catatan.deskripsi)
                    putExtra("tanggal", catatan.tanggal)
                    putExtra("waktu", catatan.waktu)
                }
                startActivity(intent)
            },
            onDeleteClick = { catatan ->
                showDeleteConfirmation(catatan)
            },
            onFavoritClick = { catatan -> // ✅ TAMBAHAN: Callback favorit
                toggleFavorit(catatan)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchCatatan(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadData() {
        // Ambil semua catatan
        allCatatan = preferences.getCatatanList().sortedByDescending { it.timestamp }

        // ✅ TAMBAHAN: Update status favorit dari FaforitPreferences
        allCatatan.forEach { catatan ->
            catatan.isFavorit = faforitPreferences.isCatatanFavorit(catatan.id)
        }

        adapter.updateData(allCatatan)
    }

    private fun searchCatatan(query: String) {
        val filtered = if (query.isEmpty()) {
            allCatatan
        } else {
            allCatatan.filter { it.querypencocokan(query) }
        }

        adapter.updateData(filtered)
    }

    // ✅ TAMBAHAN: Fungsi toggle favorit
    private fun toggleFavorit(catatan: Catatan) {
        // Toggle status favorit
        faforitPreferences.toggleCatatanFavorit(catatan.id)
        catatan.isFavorit = faforitPreferences.isCatatanFavorit(catatan.id)

        // Update adapter
        adapter.notifyDataSetChanged()

        // Tampilkan pesan
        val message = if (catatan.isFavorit) {
            "Ditambahkan ke favorit 🤪"
        } else {
            "Dihapus dari favorit 🥺"
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteConfirmation(catatan: Catatan) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Catatan 😟")
            .setMessage("Apakah Anda yakin ingin menghapus \"${catatan.judul}\" 🥺?")
            .setPositiveButton("Hapus 🫣") { _, _ ->
                preferences.deleteCatatan(catatan.id)
                // ✅ TAMBAHAN: Hapus juga dari favorit jika ada
                if (faforitPreferences.isCatatanFavorit(catatan.id)) {
                    faforitPreferences.toggleCatatanFavorit(catatan.id)
                }
                loadData()
            }
            .setNegativeButton("Batal 😊", null)
            .show()
    }
}