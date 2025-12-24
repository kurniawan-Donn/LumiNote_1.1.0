package com.example.LumiNote

// Mengimpor kelas Bundle untuk menyimpan state aktivitas
import android.os.Bundle
// Mengimpor ImageView untuk menampilkan gambar/ikon
import android.widget.ImageView
// Mengimpor TextView untuk menampilkan teks
import android.widget.TextView
// Mengimpor Toast untuk menampilkan pesan singkat kepada pengguna
import android.widget.Toast
// Mengimpor AppCompatActivity sebagai kelas dasar untuk kompatibilitas
import androidx.appcompat.app.AppCompatActivity
// Mengimpor UUID untuk menghasilkan identifier unik
import java.util.UUID

// Deklarasi kelas CatatanActivity yang mewarisi AppCompatActivity
class CatatanActivity : AppCompatActivity() {

    // ===============================
    // View Components
    // ===============================
    private lateinit var backButton: ImageView       // Tombol untuk kembali ke layar sebelumnya
    private lateinit var saveButton: ImageView       // Tombol untuk menyimpan catatan
    private lateinit var noteTitle: TextView         // TextView untuk menampilkan judul catatan
    private lateinit var dateTimeText: TextView      // TextView untuk menampilkan tanggal dan waktu
    private lateinit var noteContent: TextView       // TextView untuk menampilkan isi catatan

    // ===============================
    // Data & Preferences
    // ===============================
    private lateinit var catatanPreferences: CatatanPreferences // Kelas untuk menyimpan data catatan secara lokal

    // ===============================
    // State / Intent Data
    // ===============================
    private var itemId: String? = null       // ID catatan (null = tambah baru, ada = edit)
    private var judul: String? = null        // Judul catatan (dari Intent)
    private var deskripsi: String? = null    // Deskripsi catatan (dari Intent)
    private var note: String? = null         // Isi catatan (dari Intent)
    private var tanggal: String? = null      // Tanggal catatan (dari Intent)
    private var waktu: String? = null        // Waktu catatan (dari Intent)

    // Override fungsi onCreate yang dipanggil saat aktivitas dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        // Memanggil implementasi onCreate dari kelas induk
        super.onCreate(savedInstanceState)
        // Menetapkan layout aktivitas dari file XML activity_catatan
        setContentView(R.layout.activity_catatan)

        // Inisialisasi penyimpanan data catatan dengan konteks aktivitas saat ini
        catatanPreferences = CatatanPreferences(this)

        // Inisialisasi semua view yang dideklarasikan
        initViews()

        // Sembunyikan status bar & navigation bar untuk pengalaman fullscreen
        hideSystemUI()

        // Ambil data yang dikirim melalui Intent (untuk mode edit atau preset data baru)
        getIntentData()

        // Atur tampilan berdasarkan mode (edit atau tambah baru)
        setupUI()

        // Pasang event listener pada tombol-tombol
        setupListeners()
    }

    // ===============================
    // Inisialisasi View
    // ===============================
    private fun initViews() {
        // Menghubungkan variabel backButton dengan view di layout yang memiliki ID backButton
        backButton = findViewById(R.id.backButton)
        // Menghubungkan variabel saveButton dengan view di layout yang memiliki ID saveButton
        saveButton = findViewById(R.id.saveButton)
        // Menghubungkan variabel noteTitle dengan view di layout yang memiliki ID noteTitle
        noteTitle = findViewById(R.id.noteTitle)
        // Menghubungkan variabel dateTimeText dengan view di layout yang memiliki ID dateTimeText
        dateTimeText = findViewById(R.id.dateTimeText)
        // Menghubungkan variabel noteContent dengan view di layout yang memiliki ID noteContent
        noteContent = findViewById(R.id.noteContent)
    }

    // ===============================
    // Ambil data dari Intent
    // ===============================
    private fun getIntentData() {
        // Mengambil ID catatan dari Intent (jika ada)
        itemId = intent.getStringExtra("id")
        // Mengambil judul catatan dari Intent (jika ada)
        judul = intent.getStringExtra("judul")
        // Mengambil deskripsi catatan dari Intent (jika ada)
        deskripsi = intent.getStringExtra("deskripsi")
        // Mengambil isi catatan dari Intent (jika ada)
        note = intent.getStringExtra("note")
        // Mengambil tanggal catatan dari Intent (jika ada)
        tanggal = intent.getStringExtra("tanggal")
        // Mengambil waktu catatan dari Intent (jika ada)
        waktu = intent.getStringExtra("waktu")
    }

    // ===============================
    // Setup UI (Edit / Tambah)
    // ===============================
    private fun setupUI() {
        // Cek apakah itemId tidak null (berarti mode edit)
        if (itemId != null) {
            // Mode EDIT - memuat data catatan yang sudah ada
            loadExistingCatatan()
        } else {
            // Mode TAMBAH BARU
            // Set teks judul: gunakan judul dari Intent jika ada, atau default "JUDUL CATATAN"
            noteTitle.text = judul ?: "JUDUL CATATAN"

            // Tampilkan tanggal & waktu jika ada dari Intent
            val tanggalText = tanggal ?: ""  // Gunakan tanggal dari Intent atau string kosong
            val waktuText = waktu ?: ""      // Gunakan waktu dari Intent atau string kosong

            // Format teks tanggal dan waktu berdasarkan ketersediaan data
            dateTimeText.text = when {
                // Jika kedua tanggal dan waktu ada, tampilkan keduanya dengan baris baru
                tanggalText.isNotEmpty() && waktuText.isNotEmpty() -> "$tanggalText\n$waktuText"
                // Jika hanya tanggal yang ada, tampilkan tanggal saja
                tanggalText.isNotEmpty() -> tanggalText
                // Jika hanya waktu yang ada, tampilkan waktu saja
                waktuText.isNotEmpty() -> waktuText
                // Jika tidak ada keduanya, tampilkan pesan default
                else -> "Tanpa tanggal & waktu"
            }
        }
    }

    // ===============================
    // Load data catatan lama (EDIT)
    // ===============================
    private fun loadExistingCatatan() {
        // Mengambil data catatan dari preferences berdasarkan ID
        val catatan = catatanPreferences.getCatatanById(itemId!!) ?: return // Jika null, keluar dari fungsi

        // Set teks judul dengan judul dari objek catatan
        noteTitle.text = catatan.judul
        // Set teks isi catatan dengan note dari objek catatan
        noteContent.text = catatan.note
        // Set teks tanggal dan waktu dengan format yang sudah ditentukan dari objek catatan
        dateTimeText.text = catatan.getFormatTanggal()

        // Simpan data ke variabel untuk digunakan saat update
        judul = catatan.judul
        tanggal = catatan.tanggal
        waktu = catatan.waktu
    }

    // ===============================
    // Event Listener
    // ===============================
    private fun setupListeners() {
        // Tombol back: menutup aktivitas saat ini dan kembali ke sebelumnya
        backButton.setOnClickListener { finish() }
        // Tombol save: menyimpan atau mengupdate catatan
        saveButton.setOnClickListener { saveCatatan() }
    }

    // ===============================
    // Simpan / Update Catatan
    // ===============================
    private fun saveCatatan() {
        // Ambil teks dari noteContent, hilangkan spasi di awal dan akhir
        val isiCatatan = noteContent.text.toString().trim()

        // Validasi input
        // Cek apakah judul kosong atau null
        if (judul.isNullOrEmpty()) {
            // Tampilkan pesan toast jika judul kosong
            Toast.makeText(this, "Judul tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return // Keluar dari fungsi tanpa menyimpan
        }
        // Cek apakah isi catatan kosong
        if (isiCatatan.isEmpty()) {
            // Tampilkan pesan toast jika isi catatan kosong
            Toast.makeText(this, "Isi catatan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return // Keluar dari fungsi tanpa menyimpan
        }

        // Buat objek catatan baru dengan data yang ada
        val catatan = Catatan(
            id = itemId ?: UUID.randomUUID().toString(), // Jika itemId null, generate ID baru
            judul = judul.orEmpty(),      // Gunakan judul atau string kosong jika null
            deskripsi = deskripsi.orEmpty(), // Gunakan deskripsi atau string kosong jika null
            note = isiCatatan,             // Isi catatan dari input pengguna
            tanggal = tanggal,             // Tanggal (bisa null)
            waktu = waktu                  // Waktu (bisa null)
        )

        // Simpan atau update berdasarkan apakah itemId ada (edit) atau tidak (tambah baru)
        if (itemId != null) {
            // Mode EDIT: update catatan yang sudah ada
            catatanPreferences.updateCatatan(catatan)
            // Tampilkan pesan sukses update
            Toast.makeText(this, "Catatan berhasil diupdate", Toast.LENGTH_SHORT).show()
        } else {
            // Mode TAMBAH BARU: tambahkan catatan baru
            catatanPreferences.addCatatan(catatan)
            // Tampilkan pesan sukses simpan
            Toast.makeText(this, "Catatan berhasil disimpan", Toast.LENGTH_SHORT).show()
        }

        // Tutup aktivitas dan kembali ke layar sebelumnya
        finish()
    }
}