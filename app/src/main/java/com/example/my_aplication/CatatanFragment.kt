package com.example.my_aplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CatatanFragment : Fragment() {

    private lateinit var viewModel: CatatanViewModel
    private lateinit var adapter: CatatanAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_catatan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[CatatanViewModel::class.java]

        // Initialize views

        recyclerView = view.findViewById(R.id.rv_catatan)
        searchEditText = view.findViewById(R.id.et_search)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup search
        setupSearch()

        // Observe data
        observeData()
    }

    override fun onResume() {
        super.onResume()
        // Reload data ketika kembali ke fragment
        viewModel.loadCatatan()
    }

    private fun setupRecyclerView() {
        adapter = CatatanAdapter(
            catatanList = emptyList(),
            onItemClick = { catatan ->
                // Klik item -> langsung ke CatatanActivity
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
                // Buka TambahCatatanActivity untuk edit judul & deskripsi
                val intent = Intent(requireContext(), TambahCatatanActivity::class.java).apply {
                    putExtra("id", catatan.id)
                    putExtra("judul", catatan.judul)
                    putExtra("deskripsi", catatan.deskripsi)
                    putExtra("tanggal", catatan.tanggal)
                    putExtra("waktu", catatan.waktu)
                    putExtra("note", catatan.note)
                }
                startActivity(intent)
            },
            onDeleteClick = { catatan ->
                showDeleteConfirmation(catatan)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchCatatan(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeData() {
        viewModel.filteredCatatanList.observe(viewLifecycleOwner) { catatanList ->
            adapter.updateData(catatanList)
        }
    }

    private fun showDeleteConfirmation(catatan: Catatan) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Catatan")
            .setMessage("Apakah Anda yakin ingin menghapus \"${catatan.judul}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteCatatan(catatan.id)
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}