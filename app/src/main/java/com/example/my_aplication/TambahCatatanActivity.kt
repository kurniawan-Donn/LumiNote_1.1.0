package com.example.my_aplication

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class TambahCatatanActivity : AppCompatActivity() {

    private lateinit var etJudul: EditText
    private lateinit var layoutTanggal: LinearLayout
    private lateinit var tvTanggal: TextView
    private lateinit var layoutWaktu: LinearLayout
    private lateinit var tvWaktu: TextView
    private lateinit var btnLanjut: Button
    private lateinit var etDeskripsiPreview : EditText

    private var selectedTanggal: String? = null
    private var selectedWaktu: String? = null

    // ===============================
    // Variable untuk mode Edit - TAMBAHKAN INI
    // ===============================

    private var isEditMode = false
    private var editCatatanId: String? = null
    private var editNote: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_catatan)

        // Initialize views
        etJudul = findViewById(R.id.et_judul_catatan)
        etDeskripsiPreview = findViewById(R.id.et_Deskripsi)
        layoutTanggal = findViewById(R.id.layout_tanggal_catatan)
        tvTanggal = findViewById(R.id.tv_tanggal_catatan)
        layoutWaktu = findViewById(R.id.layout_waktu_catatan)
        tvWaktu = findViewById(R.id.tv_waktu_catatan)
        btnLanjut = findViewById(R.id.btn_Lanjut)

        hideSystemUI()

        // ===============================
        // Cek apakah mode Edit atau Tambah
        // ===============================
        checkEditMode()

        // Setup listeners
        setupDatePicker()
        setupTimePicker()
        setupLanjutButton()
    }


    // ===============================
// Cek apakah ini mode Edit - TAMBAHKAN FUNGSI INI
// ===============================
    private fun checkEditMode() {
        // Ambil data dari Intent
        val id = intent.getStringExtra("id")
        val judul = intent.getStringExtra("judul")
        val deskripsi = intent.getStringExtra("deskripsi")
        val tanggal = intent.getStringExtra("tanggal")
        val waktu = intent.getStringExtra("waktu")
        val note = intent.getStringExtra("note")

        // Jika ada ID, berarti ini mode Edit
        if (id != null) {
            isEditMode = true
            editCatatanId = id
            editNote = note

            // Isi form dengan data yang ada
            etJudul.setText(judul)
            etDeskripsiPreview.setText(deskripsi)

            if (tanggal != null) {
                selectedTanggal = tanggal
                tvTanggal.text = tanggal
            }

            if (waktu != null) {
                selectedWaktu = waktu
                tvWaktu.text = waktu
            }

            // Ubah text tombol menjadi "Update"
            btnLanjut.text = "Update"
        } else {
            // Mode Tambah Baru
            isEditMode = false
            btnLanjut.text = "Lanjut"
        }
    }

    private fun setupDatePicker() {
        layoutTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                selectedTanggal = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                tvTanggal.text = selectedTanggal
            }, year, month, day).show()
        }
    }

    private fun setupTimePicker() {
        layoutWaktu.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                selectedWaktu = String.format("%02d:%02d", selectedHour, selectedMinute)
                tvWaktu.text = selectedWaktu
            }, hour, minute, true).show()
        }
    }

    private fun setupLanjutButton() {
        btnLanjut.setOnClickListener {
            val judul = etJudul.text.toString().trim()
            val deskripsi = etDeskripsiPreview.text.toString().trim()

            // Validasi
            if (judul.isEmpty()) {
                Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===============================
            // Cek mode: Edit atau Tambah
            // ===============================
            if (isEditMode && editCatatanId != null) {
                // ===============================
                // MODE EDIT: Update catatan yang ada
                // ===============================
                val catatanPreferences = CatatanPreferences(this)
                val catatanUpdate = Catatan(
                    id = editCatatanId!!, // Gunakan ID yang sama
                    judul = judul,
                    deskripsi = deskripsi,
                    note = editNote ?: "", // Pertahankan note lama
                    tanggal = selectedTanggal,
                    waktu = selectedWaktu,
                    timestamp = System.currentTimeMillis()
                )

                // Update ke preferences
                catatanPreferences.updateCatatan(catatanUpdate)
                Toast.makeText(this, "Catatan berhasil diupdate", Toast.LENGTH_SHORT).show()
                finish()

            } else {
                // ===============================
                // MODE TAMBAH: Pindah ke CatatanActivity untuk isi note
                // ===============================
                val intent = Intent(this, CatatanActivity::class.java).apply {
                    putExtra("judul", judul)
                    putExtra("deskripsi", deskripsi)
                    putExtra("tanggal", selectedTanggal)
                    putExtra("waktu", selectedWaktu)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}