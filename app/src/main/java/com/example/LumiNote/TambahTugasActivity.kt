// Deklarasi package untuk organisasi kode
package com.example.LumiNote

// Mengimpor DatePickerDialog untuk memilih tanggal dari kalender
import android.app.DatePickerDialog
// Mengimpor TimePickerDialog untuk memilih waktu dari clock
import android.app.TimePickerDialog
// Mengimpor Bundle untuk menyimpan state aktivitas
import android.os.Bundle
// Mengimpor Button untuk tombol interaktif
import android.widget.Button
// Mengimpor EditText untuk input teks
import android.widget.EditText
// Mengimpor LinearLayout sebagai container untuk view
import android.widget.LinearLayout
// Mengimpor TextView untuk menampilkan teks
import android.widget.TextView
// Mengimpor Toast untuk menampilkan pesan singkat
import android.widget.Toast
// Mengimpor AppCompatActivity sebagai kelas dasar untuk kompatibilitas
import androidx.appcompat.app.AppCompatActivity
// Mengimpor kelas Calendar untuk manipulasi tanggal dan waktu
import java.util.*

// Deklarasi kelas TambahTugasActivity yang mewarisi AppCompatActivity
class TambahTugasActivity : AppCompatActivity() {

    // ===============================
    // Deklarasi View Components
    // ===============================
    private lateinit var etJudul: EditText           // Input untuk judul tugas
    private lateinit var etIsiTugas: EditText        // Input untuk deskripsi/isi tugas
    private lateinit var layoutTanggal: LinearLayout // Layout yang bisa diklik untuk memilih tanggal
    private lateinit var tvTanggal: TextView         // Menampilkan tanggal yang dipilih
    private lateinit var layoutWaktu: LinearLayout   // Layout yang bisa diklik untuk memilih waktu
    private lateinit var tvWaktu: TextView           // Menampilkan waktu yang dipilih
    private lateinit var btnSimpan: Button           // Tombol untuk menyimpan/update tugas

    // ===============================
    // Variabel untuk menyimpan data tanggal dan waktu yang dipilih
    // ===============================
    private var selectedTanggal: String? = null  // Format: "DD/MM/YYYY"
    private var selectedWaktu: String? = null    // Format: "HH:MM"

    // ===============================
    // Variabel untuk mengelola penyimpanan data tugas
    // ===============================
    private lateinit var tugasPreferences: TugasPreferences

    // ===============================
    // Variable untuk mode Edit
    // ===============================
    private var isEditMode = false        // Flag untuk menentukan mode (edit atau tambah)
    private var editTugasId: String? = null // ID tugas yang sedang diedit (null untuk mode tambah)
    private var editIsSelesai: Boolean = false // Status penyelesaian tugas (untuk mode edit)

    // Fungsi onCreate dipanggil saat aktivitas dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        // Memanggil implementasi onCreate dari kelas induk
        super.onCreate(savedInstanceState)
        // Menetapkan layout aktivitas dari file XML activity_tambah_tugas
        setContentView(R.layout.activity_tambah_tugas)

        // ===============================
        // Init views - Menghubungkan variabel dengan komponen di layout
        // ===============================
        etJudul = findViewById(R.id.et_judul_tugas)
        etIsiTugas = findViewById(R.id.et_isi_tugas)
        layoutTanggal = findViewById(R.id.layout_tanggal_tugas)
        tvTanggal = findViewById(R.id.tv_tanggal_tugas)
        layoutWaktu = findViewById(R.id.layout_waktu_tugas)
        tvWaktu = findViewById(R.id.tv_waktu_tugas)
        btnSimpan = findViewById(R.id.btn_simpan)

        // Menyembunyikan sistem UI (status bar dan navigation bar) untuk pengalaman fullscreen
        hideSystemUI()

        // ===============================
        // Init preferences - Inisialisasi penyimpanan data tugas
        // ===============================
        tugasPreferences = TugasPreferences(this)

        // ===============================
        // Cek apakah mode Edit atau Tambah
        // ===============================
        checkEditMode()

        // ===============================
        // Setup listeners - Mengatur listener untuk komponen interaktif
        // ===============================
        setupDatePicker()   // Mengatur date picker untuk pemilihan tanggal
        setupTimePicker()   // Mengatur time picker untuk pemilihan waktu
        setupSimpanButton() // Mengatur aksi tombol simpan/update
    }

    // ===============================
    // Cek apakah ini mode Edit
    // ===============================
    private fun checkEditMode() {
        // Ambil data dari Intent yang dikirim dari activity sebelumnya
        val id = intent.getStringExtra("id")           // ID tugas (null jika tambah baru)
        val judul = intent.getStringExtra("judul")     // Judul tugas
        val deskripsi = intent.getStringExtra("deskripsi") // Deskripsi tugas
        val tanggal = intent.getStringExtra("tanggal") // Tanggal dan waktu gabungan
        val isSelesai = intent.getBooleanExtra("isSelesai", false) // Status penyelesaian

        // Jika ada ID, berarti ini mode Edit (mengedit tugas yang sudah ada)
        if (id != null) {
            isEditMode = true          // Set flag edit mode ke true
            editTugasId = id           // Simpan ID tugas yang akan diedit
            editIsSelesai = isSelesai  // Simpan status penyelesaian

            // Isi form dengan data yang ada (pre-populate form)
            etJudul.setText(judul)
            etIsiTugas.setText(deskripsi)

            // Parse tanggal dan waktu dari format "DD/MM/YYYY HH:MM"
            if (tanggal != null) {
                // Pisahkan string tanggal menjadi bagian-bagian (tanggal dan waktu)
                val parts = tanggal.split(" ")
                if (parts.isNotEmpty()) {
                    // Bagian pertama adalah tanggal (format: DD/MM/YYYY)
                    selectedTanggal = parts[0]
                    tvTanggal.text = selectedTanggal // Tampilkan tanggal di TextView

                    if (parts.size > 1) {
                        // Bagian kedua adalah waktu (format: HH:MM)
                        selectedWaktu = parts[1]
                        tvWaktu.text = selectedWaktu // Tampilkan waktu di TextView
                    }
                }
            }

            // Ubah teks tombol menjadi "Update" untuk menunjukkan mode edit
            btnSimpan.text = "Update"
        } else {
            // Mode Tambah Baru (tidak ada data dari Intent)
            isEditMode = false
            btnSimpan.text = "Simpan" // Teks default untuk tombol
        }
    }

    // ===============================
    // Date Picker - Mengatur pemilih tanggal
    // ===============================
    private fun setupDatePicker() {
        // Menambahkan click listener pada layout tanggal
        layoutTanggal.setOnClickListener {
            // Mendapatkan tanggal saat ini sebagai default
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)  // Month dimulai dari 0 (Januari = 0)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Membuat dan menampilkan DatePickerDialog
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format tanggal: DD/MM/YYYY (day dengan 2 digit, month+1, year 4 digit)
                selectedTanggal = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                tvTanggal.text = selectedTanggal // Update TextView dengan tanggal yang dipilih
            }, year, month, day).show()
        }
    }

    // ===============================
    // Time Picker - Mengatur pemilih waktu
    // ===============================
    private fun setupTimePicker() {
        // Menambahkan click listener pada layout waktu
        layoutWaktu.setOnClickListener {
            // Mendapatkan waktu saat ini sebagai default
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY) // Format 24 jam
            val minute = calendar.get(Calendar.MINUTE)

            // Membuat dan menampilkan TimePickerDialog
            // Parameter true: menggunakan format 24 jam
            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                // Format waktu: HH:MM (2 digit untuk jam dan menit)
                selectedWaktu = String.format("%02d:%02d", selectedHour, selectedMinute)
                tvWaktu.text = selectedWaktu // Update TextView dengan waktu yang dipilih
            }, hour, minute, true).show()
        }
    }

    // ===============================
    // Simpan atau Update tugas
    // ===============================
    private fun setupSimpanButton() {
        btnSimpan.setOnClickListener {
            // Mengambil teks dari input field dan menghapus spasi di awal/akhir
            val judul = etJudul.text.toString().trim()
            val isiTugas = etIsiTugas.text.toString().trim()

            // ===============================
            // Validasi input
            // ===============================
            if (judul.isEmpty()) {
                Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Keluar dari fungsi jika validasi gagal
            }

            if (isiTugas.isEmpty()) {
                Toast.makeText(this, "Isi tugas tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedTanggal.isNullOrEmpty() || selectedWaktu.isNullOrEmpty()) {
                Toast.makeText(this, "Tanggal dan waktu harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ===============================
            // Gabungkan tanggal dan waktu menjadi satu string
            // Format: "DD/MM/YYYY HH:MM"
            // ===============================
            val tanggalWaktu = "$selectedTanggal $selectedWaktu"

            // ===============================
            // Cek mode: Edit atau Tambah
            // ===============================
            if (isEditMode && editTugasId != null) {
                // ===============================
                // MODE EDIT: Update tugas yang ada
                // ===============================
                val tugasUpdate = Tugas(
                    id = editTugasId!!, // Gunakan ID yang sama dengan tugas yang diedit
                    judul = judul,
                    deskripsi = isiTugas,
                    tanggal = tanggalWaktu,
                    waktu = selectedWaktu,
                    isSelesai = editIsSelesai, // Pertahankan status checklist dari data lama
                    timestamp = System.currentTimeMillis() // Update timestamp ke waktu sekarang
                )

                // Update data tugas di penyimpanan lokal
                tugasPreferences.updateTugas(tugasUpdate)
                Toast.makeText(this, "Tugas berhasil diupdate", Toast.LENGTH_SHORT).show()

            } else {
                // ===============================
                // MODE TAMBAH: Buat tugas baru
                // ===============================
                val tugasBaru = Tugas(
                    // id akan otomatis di-generate UUID di data class (asumsi)
                    judul = judul,
                    deskripsi = isiTugas,
                    tanggal = tanggalWaktu,
                    waktu = selectedWaktu,
                    isSelesai = false, // Tugas baru defaultnya belum selesai
                    timestamp = System.currentTimeMillis() // Timestamp waktu pembuatan
                )

                // Simpan tugas baru ke penyimpanan lokal
                tugasPreferences.addTugas(tugasBaru)
                Toast.makeText(this, "Tugas berhasil disimpan", Toast.LENGTH_SHORT).show()
            }

            // Kembali ke activity sebelumnya (TugasFragment)
            finish()
        }
    }
}