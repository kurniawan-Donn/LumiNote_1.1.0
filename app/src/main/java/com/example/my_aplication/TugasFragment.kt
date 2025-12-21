package com.example.my_aplication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TugasFragment : Fragment() {

    private lateinit var layoutTugasSelesai: LinearLayout
    private lateinit var icDiselesaikan: ImageView
    private lateinit var tvDiselesaikan: TextView
    private lateinit var rvTugasSelesai: RecyclerView
    private lateinit var tugasSelesaiAdapter: TugasAdapter
    private lateinit var rvTugas: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var tugasPreferences: TugasPreferences
    private lateinit var tugasAdapter: TugasAdapter

    // tampilan tugas selesai
    private var tampilkanTugasSelesai = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tugas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tugasPreferences = TugasPreferences(requireContext())
        layoutTugasSelesai = view.findViewById(R.id.layout_tugas_selesai)
        icDiselesaikan = view.findViewById(R.id.ic_diselesaikan)
        tvDiselesaikan = view.findViewById(R.id.tv_diselesaikan)
        rvTugasSelesai = view.findViewById(R.id.rv_tugas_selesai)
        rvTugas = view.findViewById(R.id.rv_tugas)
        etSearch = view.findViewById(R.id.et_search)

        setupSearch()
        setupRecyclerView()
        setupRecyclerViewSelesai()
        setupClickListeners()
        loadData()

        // Tampilkan RecyclerView tugas selesai langsung
        animasiTugasSelesai(tampilkanTugasSelesai)
        updateCompletedText(tampilkanTugasSelesai)
    }
    private fun setupRecyclerView() {
        tugasAdapter = TugasAdapter(
            listTugas = mutableListOf(),
            onEditClick = { tugas -> openEditTugas(tugas) },
            onDeleteClick = { tugas -> deleteTugas(tugas) },
            onCheckedChange = { tugas, isChecked -> updateStatusTugas(tugas, isChecked) }
        )
        rvTugas.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tugasAdapter
        }
    }

    private fun setupRecyclerViewSelesai() {
        tugasSelesaiAdapter = TugasAdapter(
            listTugas = mutableListOf(),
            onEditClick = { tugas -> openEditTugas(tugas) },
            onDeleteClick = { tugas -> deleteTugas(tugas) },
            onCheckedChange = { tugas, isChecked -> updateStatusTugas(tugas, isChecked) }
        )

        rvTugasSelesai.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tugasSelesaiAdapter
        }
    }
    // Setup Click Listenerer
    private fun setupClickListeners() {
        layoutTugasSelesai.setOnClickListener {
            tampilkanTugasSelesai = !tampilkanTugasSelesai
            animasiTugasSelesai(tampilkanTugasSelesai)
            updateCompletedText(tampilkanTugasSelesai)
        }
    }
    private fun animasiTugasSelesai(ditunjukkan: Boolean) {
        icDiselesaikan.animate()
            .rotation(if (ditunjukkan) 180f else 0f)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        if (ditunjukkan) {
            rvTugasSelesai.apply {
                alpha = 0f
                isVisible = true
                animate().alpha(1f).setDuration(300).start()
            }
        } else {
            rvTugasSelesai.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction { rvTugasSelesai.isVisible = false }
                .start()
        }
    }
    private fun updateCompletedText(isExpanded: Boolean) {
        val completedCount = tugasSelesaiAdapter.itemCount
        tvDiselesaikan.text = if (isExpanded) "Diselesaikan ($completedCount)" else "Diselesaikan"
    }
    private fun loadData() {
        val allTugas = tugasPreferences.getAllTugas()
        val belumSelesai = allTugas.filter { !it.isSelesai }
        val selesai = allTugas.filter { it.isSelesai }

        tugasAdapter.updateData(belumSelesai)
        tugasSelesaiAdapter.updateData(selesai)

        updateCompletedText(tampilkanTugasSelesai)
    }
    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(inputteks: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(inputteks: CharSequence?, start: Int, before: Int, count: Int) {
                val query = inputteks.toString().lowercase()
                val allTugas = tugasPreferences.getAllTugas()
                val belumSelesaiList = allTugas.filter { !it.isSelesai && it.matchesQuery(query) }
                val selesaiList = allTugas.filter { it.isSelesai && it.matchesQuery(query) }
                tugasAdapter.updateData(belumSelesaiList)
                tugasSelesaiAdapter.updateData(selesaiList)
                rvTugasSelesai.isVisible = selesaiList.isNotEmpty()
                updateCompletedText(tampilkanTugasSelesai)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    private fun openEditTugas(tugas: Tugas) {
        val intent = Intent(requireContext(), TambahTugasActivity::class.java).apply {
            putExtra("id", tugas.id)
            putExtra("judul", tugas.judul)
            putExtra("deskripsi", tugas.deskripsi)
            putExtra("tanggal", tugas.tanggal)
            putExtra("isSelesai", tugas.isSelesai)
        }
        startActivity(intent)
    }
    private fun deleteTugas(tugas: Tugas) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus Tugas")
            .setMessage("Apakah Anda yakin ingin menghapus \"${tugas.judul}\"?")
            .setPositiveButton("Hapus") { _, _ ->
                tugasPreferences.deleteTugas(tugas.id)
                loadData()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    private fun updateStatusTugas(tugas: Tugas, isChecked: Boolean) {
        val updatedTugas = tugas.copy(isSelesai = isChecked)
        tugasPreferences.updateTugas(updatedTugas)
        loadData()
    }
    override fun onResume() {
        super.onResume()
        loadData()
    }
}