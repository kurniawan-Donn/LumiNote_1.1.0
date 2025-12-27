package com.example.LumiNote

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ArsipActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var btnArsipAction: ImageButton
    private lateinit var etSearch: EditText
    private lateinit var rvArsip: RecyclerView
    private lateinit var layoutEmpty: LinearLayout

    private lateinit var arsipAdapter: ArsipAdapter
    private lateinit var arsipPreferences: ArsipPreferences
    private lateinit var catatanPreferences: CatatanPreferences
    private lateinit var tugasPreferences: TugasPreferences

    private var allArsipList = listOf<ArsipItem>()
    private var filteredList = listOf<ArsipItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arsip)

        initViews()
        initPreferences()
        setupRecyclerView()
        setupListeners()
        loadArsipData()
    }

    override fun onResume() {
        super.onResume()
        loadArsipData()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        btnArsipAction = findViewById(R.id.btnStar)
        etSearch = findViewById(R.id.etSearch)
        rvArsip = findViewById(R.id.rvPenting)
        layoutEmpty = findViewById(R.id.layoutEmpty)
    }

    private fun initPreferences() {
        arsipPreferences = ArsipPreferences(this)
        catatanPreferences = CatatanPreferences(this)
        tugasPreferences = TugasPreferences(this)
    }

    private fun setupRecyclerView() {
        arsipAdapter = ArsipAdapter(
            arsipList = emptyList(),
            onPulihkanClick = { item ->
                showPulihkanDialog(item)
            },
            onHapusClick = { item ->
                showHapusDialog(item)
            }
        )

        rvArsip.apply {
            layoutManager = LinearLayoutManager(this@ArsipActivity)
            adapter = arsipAdapter
        }
    }

    private fun setupListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Button action (dalam perbaikan)
        btnArsipAction.setOnClickListener {
            Toast.makeText(this, "Fitur dalam perbaikan", Toast.LENGTH_SHORT).show()
        }

        // Search
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterData(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadArsipData() {
        val arsipList = mutableListOf<ArsipItem>()

        // Load Catatan Arsip
        val arsipCatatanIds = arsipPreferences.getArsipCatatan()
        val allCatatan = catatanPreferences.getCatatanList()
        val catatanArsip = allCatatan.filter { it.id in arsipCatatanIds }

        catatanArsip.forEach { catatan ->
            arsipList.add(ArsipItem.fromCatatan(catatan))
        }

        // Load Tugas Arsip
        val arsipTugasIds = arsipPreferences.getArsipTugas()
        val allTugas = tugasPreferences.getAllTugas()
        val tugasArsip = allTugas.filter { it.id in arsipTugasIds }

        tugasArsip.forEach { tugas ->
            arsipList.add(ArsipItem.fromTugas(tugas))
        }

        // Sort by timestamp (newest first)
        allArsipList = arsipList.sortedByDescending { it.timestamp }
        filteredList = allArsipList

        updateUI()
    }

    private fun filterData(query: String) {
        filteredList = if (query.isEmpty()) {
            allArsipList
        } else {
            allArsipList.filter { it.matchesQuery(query) }
        }
        updateUI()
    }

    private fun updateUI() {
        if (filteredList.isEmpty()) {
            rvArsip.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        } else {
            rvArsip.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
            arsipAdapter.updateData(filteredList)
        }
    }

    private fun showPulihkanDialog(item: ArsipItem) {
        AlertDialog.Builder(this)
            .setTitle("Pulihkan ${item.tipe}")
            .setMessage("Apakah Anda yakin ingin memulihkan \"${item.judul}\"?")
            .setPositiveButton("Pulihkan") { dialog, _ ->
                pulihkanItem(item)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showHapusDialog(item: ArsipItem) {
        AlertDialog.Builder(this)
            .setTitle("Hapus ${item.tipe}")
            .setMessage("Apakah Anda yakin ingin menghapus \"${item.judul}\" secara permanen?")
            .setPositiveButton("Hapus") { dialog, _ ->
                hapusItem(item)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun pulihkanItem(item: ArsipItem) {
        when (item.tipe) {
            "Catatan" -> {
                arsipPreferences.pulihkanCatatan(item.id)
                Toast.makeText(this, "Catatan dipulihkan", Toast.LENGTH_SHORT).show()
            }
            "Tugas" -> {
                arsipPreferences.pulihkanTugas(item.id)
                Toast.makeText(this, "Tugas dipulihkan", Toast.LENGTH_SHORT).show()
            }
        }
        loadArsipData()
    }

    private fun hapusItem(item: ArsipItem) {
        when (item.tipe) {
            "Catatan" -> {
                // Hapus dari arsip
                arsipPreferences.pulihkanCatatan(item.id)
                // Hapus data asli
                catatanPreferences.deleteCatatan(item.id)
                Toast.makeText(this, "Catatan dihapus permanen", Toast.LENGTH_SHORT).show()
            }
            "Tugas" -> {
                // Hapus dari arsip
                arsipPreferences.pulihkanTugas(item.id)
                // Hapus data asli
                tugasPreferences.deleteTugas(item.id)
                Toast.makeText(this, "Tugas dihapus permanen", Toast.LENGTH_SHORT).show()
            }
        }
        loadArsipData()
    }
}