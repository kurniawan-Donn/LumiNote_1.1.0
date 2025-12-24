package com.example.LumiNote  // Mendeklarasikan package/namespace untuk aplikasi

// ===============================
// IMPORTS
// ===============================
import android.app.DatePickerDialog  // Untuk dialog pemilih tanggal
import android.app.TimePickerDialog  // Untuk dialog pemilih waktu
import android.content.Intent  // Untuk berpindah antar activity
import android.os.Bundle  // Untuk menyimpan state activity
import android.widget.Button  // Komponen button
import android.widget.EditText  // Komponen input teks
import android.widget.LinearLayout  // Layout untuk mengelompokkan komponen
import android.widget.TextView  // Untuk menampilkan teks
import android.widget.Toast  // Untuk menampilkan pesan singkat
import androidx.appcompat.app.AppCompatActivity  // Base class untuk activity
import java.util.Calendar  // Untuk operasi tanggal dan waktu

// ===============================
// CLASS TAMBAH CATATAN ACTIVITY
// ===============================
class TambahCatatanActivity : AppCompatActivity() {  // Mendefinisikan class activity untuk menambah/mengedit catatan

    // ===============================
    // DEKLARASI VIEW COMPONENTS
    // ===============================
    private lateinit var etJudul: EditText  // Input untuk judul catatan
    private lateinit var layoutTanggal: LinearLayout  // Layout container untuk pilih tanggal
    private lateinit var tvTanggal: TextView  // Text view untuk menampilkan tanggal terpilih
    private lateinit var layoutWaktu: LinearLayout  // Layout container untuk pilih waktu
    private lateinit var tvWaktu: TextView  // Text view untuk menampilkan waktu terpilih
    private lateinit var btnLanjut: Button  // Button untuk lanjut/simpan
    private lateinit var etDeskripsiPreview: EditText  // Input untuk deskripsi singkat catatan

    // ===============================
    // VARIABEL DATA SEMENTARA
    // ===============================
    private var tanggalDipilih: String? = null  // Menyimpan tanggal yang dipilih user (nullable)
    private var waktuDipilih: String? = null  // Menyimpan waktu yang dipilih user (nullable)

    // ===============================
    // VARIABEL UNTUK MODE EDIT
    // ===============================
    private var isEditMode = false  // Flag untuk menandai apakah mode edit atau tambah
    private var editCatatanId: String? = null  // ID catatan yang akan diedit (jika mode edit)
    private var editNote: String? = null  // Isi lengkap catatan yang akan diedit (jika mode edit)

    // ===============================
    // METHOD onCreate
    // Dipanggil saat activity pertama kali dibuat
    // ===============================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)  // Memanggil implementasi parent class
        setContentView(R.layout.activity_tambah_catatan)  // Men-set layout UI dari XML

        // Inisialisasi view dengan komponen di layout
        etJudul = findViewById(R.id.et_judul_catatan)  // Mengambil referensi EditText judul dari layout
        etDeskripsiPreview = findViewById(R.id.et_Deskripsi)  // Mengambil referensi EditText deskripsi
        layoutTanggal = findViewById(R.id.layout_tanggal_catatan)  // Mengambil referensi layout tanggal
        tvTanggal = findViewById(R.id.tv_tanggal_catatan)  // Mengambil referensi TextView tanggal
        layoutWaktu = findViewById(R.id.layout_waktu_catatan)  // Mengambil referensi layout waktu
        tvWaktu = findViewById(R.id.tv_waktu_catatan)  // Mengambil referensi TextView waktu
        btnLanjut = findViewById(R.id.btn_Lanjut)  // Mengambil referensi button lanjut

        hideSystemUI()  // Memanggil method untuk menyembunyikan system UI (jika ada)

        // ===============================
        // CEK APAKAH MODE EDIT ATAU TAMBAH
        // ===============================
        checkEditMode()  // Memanggil fungsi untuk menentukan mode activity

        // Setup listener/event handler
        setupDatePicker()  // Mengatur picker tanggal
        setupTimePicker()  // Mengatur picker waktu
        setupLanjutButton()  // Mengatur aksi button lanjut/simpan
    }

    // ===============================
    // METHOD checkEditMode
    // Mengecek apakah activity dibuka dalam mode edit atau tambah baru
    // ===============================
    private fun checkEditMode() {
        val id = intent.getStringExtra("id")  // Mengambil ID catatan dari intent (jika ada)

        if (id != null) {  // Jika ID tidak null, berarti mode edit
            isEditMode = true  // Set flag edit mode ke true
            editCatatanId = id  // Simpan ID catatan yang akan diedit

            // Ambil catatan lengkap dari preferences berdasarkan ID
            val catatanPref = CatatanPreferences(this)  // Membuat objek preferences
            val catatan = catatanPref.getCatatanById(id)  // Mendapatkan catatan berdasarkan ID
            if (catatan != null) {  // Jika catatan ditemukan
                etJudul.setText(catatan.judul)  // Set teks judul dari data catatan
                etDeskripsiPreview.setText(catatan.deskripsi)  // Set teks deskripsi
                tanggalDipilih = catatan.tanggal  // Simpan tanggal dari catatan
                tvTanggal.text = tanggalDipilih  // Tampilkan tanggal di TextView
                waktuDipilih = catatan.waktu  // Simpan waktu dari catatan
                tvWaktu.text = waktuDipilih  // Tampilkan waktu di TextView
                editNote = catatan.note  // Simpan isi lengkap catatan untuk diedit
            }

            btnLanjut.text = getString(R.string.simpan)  // Ubah teks button menjadi "Simpan"
        } else {  // Jika ID null, berarti mode tambah catatan baru
            isEditMode = false  // Set flag edit mode ke false
            btnLanjut.text = getString(R.string.lanjut)  // Set teks button menjadi "Lanjut"
        }
    }

    // ===============================
    // METHOD setupDatePicker
    // Mengatur DatePickerDialog untuk memilih tanggal
    // ===============================
    private fun setupDatePicker() {
        layoutTanggal.setOnClickListener {  // Men-set listener klik pada layout tanggal
            val kalender = Calendar.getInstance()  // Mendapatkan instance kalender dengan waktu saat ini
            val tahun = kalender.get(Calendar.YEAR)  // Mengambil tahun saat ini
            val bulan = kalender.get(Calendar.MONTH)  // Mengambil bulan saat ini (0-based: Jan=0)
            val hari = kalender.get(Calendar.DAY_OF_MONTH)  // Mengambil hari saat ini

            // Membuat DatePickerDialog
            DatePickerDialog(this, { _, y, m, d ->  // Callback ketika tanggal dipilih
                // Format tanggal: DD/MM/YYYY
                tanggalDipilih = String.format("%02d/%02d/%04d", d, m + 1, y)
                tvTanggal.text = tanggalDipilih  // Menampilkan tanggal yang dipilih di TextView
            }, tahun, bulan, hari).show()  // Menampilkan dialog dengan tanggal default
        }
    }

    // ===============================
    // METHOD setupTimePicker
    // Mengatur TimePickerDialog untuk memilih waktu
    // ===============================
    private fun setupTimePicker() {
        layoutWaktu.setOnClickListener {  // Men-set listener klik pada layout waktu
            val kalender = Calendar.getInstance()  // Mendapatkan instance kalender
            val jam = kalender.get(Calendar.HOUR_OF_DAY)  // Mengambil jam saat ini (format 24 jam)
            val menit = kalender.get(Calendar.MINUTE)  // Mengambil menit saat ini

            // Membuat TimePickerDialog
            TimePickerDialog(this, { _, h, m ->  // Callback ketika waktu dipilih
                // Format waktu: HH:MM
                waktuDipilih = String.format("%02d:%02d", h, m)
                tvWaktu.text = waktuDipilih  // Menampilkan waktu yang dipilih di TextView
            }, jam, menit, true).show()  // true untuk format 24 jam, false untuk AM/PM
        }
    }

    // ===============================
    // METHOD setupLanjutButton
    // Mengatur aksi ketika button lanjut/simpan diklik
    // ===============================
    private fun setupLanjutButton() {
        btnLanjut.setOnClickListener {  // Men-set listener klik pada button
            val judul = etJudul.text.toString().trim()  // Mengambil dan membersihkan input judul
            val deskripsi = etDeskripsiPreview.text.toString().trim()  // Mengambil dan membersihkan input deskripsi

            // ===============================
            // VALIDASI INPUT
            // ===============================
            if (judul.isEmpty()) {  // Jika judul kosong
                Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // Hentikan eksekusi, tampilkan pesan error
            }

            // ===============================
            // LOGIKA UNTUK MODE EDIT
            // ===============================
            if (isEditMode && editCatatanId != null) {  // Jika dalam mode edit dan ID tersedia
                val catatanPref = CatatanPreferences(this)  // Membuat objek preferences
                // Membuat objek Catatan dengan data yang di-update
                val catatanUpdate = Catatan(
                    id = editCatatanId!!,  // ID tetap sama (non-null assertion)
                    judul = judul,  // Judul baru
                    deskripsi = deskripsi,  // Deskripsi baru
                    note = editNote ?: "",  // Isi catatan (jika null gunakan string kosong)
                    tanggal = tanggalDipilih,  // Tanggal yang dipilih
                    waktu = waktuDipilih,  // Waktu yang dipilih
                    timestamp = System.currentTimeMillis()  // Update timestamp ke waktu sekarang
                )
                catatanPref.updateCatatan(catatanUpdate)  // Menyimpan update ke preferences
                Toast.makeText(this, "Catatan berhasil diupdate", Toast.LENGTH_SHORT).show()
                finish()  // Menutup activity dan kembali ke sebelumnya
            }
            // ===============================
            // LOGIKA UNTUK MODE TAMBAH BARU
            // ===============================
            else {
                // Membuat intent untuk pindah ke CatatanActivity (untuk mengisi catatan lengkap)
                val intent = Intent(this, CatatanActivity::class.java).apply {
                    putExtra("judul", judul)  // Mengirim judul
                    putExtra("deskripsi", deskripsi)  // Mengirim deskripsi
                    putExtra("tanggal", tanggalDipilih)  // Mengirim tanggal
                    putExtra("waktu", waktuDipilih)  // Mengirim waktu
                    putExtra("note", editNote)  // Mengirim note (jika ada dari mode edit)
                }
                startActivity(intent)  // Memulai activity baru
                finish()  // Menutup activity ini
            }
        }
    }
}