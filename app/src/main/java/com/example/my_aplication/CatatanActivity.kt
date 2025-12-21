package com.example.my_aplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID

class CatatanActivity : AppCompatActivity() {

    // ===============================
    // View Components
    // ===============================
    private lateinit var backButton: ImageView
    private lateinit var saveButton: ImageView
    private lateinit var noteTitle: TextView
    private lateinit var dateTimeText: TextView
    private lateinit var noteContent: TextView

    // ===============================
    // Data & Preferences
    // ===============================
    private lateinit var catatanPreferences: CatatanPreferences

    // ===============================
    // State / Intent Data
    // ===============================
    private var itemId: String? = null       // ID catatan (null = tambah baru)
    private var judul: String? = null        // Judul catatan
    private var deskripsi: String? = null    // Deskripsi catatan
    private var note: String? = null         // Isi catatan
    private var tanggal: String? = null      // Tanggal catatan
    private var waktu: String? = null        // Waktu catatan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catatan)

        // Inisialisasi penyimpanan data catatan
        catatanPreferences = CatatanPreferences(this)

        // Inisialisasi semua view
        initViews()

        // Sembunyikan status bar & navigation bar
        hideSystemUI()

        // Ambil data dari Intent (edit / tambah)
        getIntentData()

        // Atur tampilan berdasarkan mode (edit / tambah)
        setupUI()

        // Pasang event listener
        setupListeners()
    }

    // ===============================
    // Inisialisasi View
    // ===============================
    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        saveButton = findViewById(R.id.saveButton)
        noteTitle = findViewById(R.id.noteTitle)
        dateTimeText = findViewById(R.id.dateTimeText)
        noteContent = findViewById(R.id.noteContent)
    }

    // ===============================
    // Ambil data dari Intent
    // ===============================
    private fun getIntentData() {
        itemId = intent.getStringExtra("id")
        judul = intent.getStringExtra("judul")
        deskripsi = intent.getStringExtra("deskripsi")  // TAMBAHKAN INI
        note = intent.getStringExtra("note")
        tanggal = intent.getStringExtra("tanggal")
        waktu = intent.getStringExtra("waktu")
    }

    // ===============================
    // Setup UI (Edit / Tambah)
    // ===============================
    private fun setupUI() {
        if (itemId != null) {
            // Mode EDIT
            loadExistingCatatan()
        } else {
            // Mode TAMBAH BARU
            noteTitle.text = judul ?: "JUDUL CATATAN"


            // Tampilkan tanggal & waktu jika ada
            val tanggalText = tanggal ?: ""
            val waktuText = waktu ?: ""
            dateTimeText.text = when {
                tanggalText.isNotEmpty() && waktuText.isNotEmpty() -> "$tanggalText\n$waktuText"
                tanggalText.isNotEmpty() -> tanggalText
                waktuText.isNotEmpty() -> waktuText
                else -> "Tanpa tanggal & waktu"
            }
        }
    }

    // ===============================
    // Load data catatan lama (EDIT)
    // ===============================
    private fun loadExistingCatatan() {
        val catatan = catatanPreferences.getCatatanById(itemId!!) ?: return

        noteTitle.text = catatan.judul
        noteContent.text = catatan.note
        dateTimeText.text = catatan.getFormatTanggal()

        // Simpan untuk update
        judul = catatan.judul
        tanggal = catatan.tanggal
        waktu = catatan.waktu
    }

    // ===============================
    // Event Listener
    // ===============================
    private fun setupListeners() {
        backButton.setOnClickListener { finish() }
        saveButton.setOnClickListener { saveCatatan() }
    }

    // ===============================
// Simpan / Update Catatan
// ===============================
    private fun saveCatatan() {
        val isiCatatan = noteContent.text.toString().trim()

        // Validasi input
        if (judul.isNullOrEmpty()) {
            Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (isiCatatan.isEmpty()) {
            Toast.makeText(this, "Isi catatan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // Buat objek catatan
        val catatan = Catatan(
            id = itemId ?: UUID.randomUUID().toString(),
            judul = judul.orEmpty(),
            deskripsi = deskripsi.orEmpty(),
            note = isiCatatan,
            tanggal = tanggal,
            waktu = waktu
        )

        // Simpan atau update
        if (itemId != null) {
            catatanPreferences.updateCatatan(catatan)
            Toast.makeText(this, "Catatan berhasil diupdate", Toast.LENGTH_SHORT).show()
        } else {
            catatanPreferences.addCatatan(catatan)
            Toast.makeText(this, "Catatan berhasil disimpan", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}