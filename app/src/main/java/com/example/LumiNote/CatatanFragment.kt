// Deklarasi package untuk organisasi kode
package com.example.LumiNote

// Mengimpor AlertDialog untuk menampilkan dialog konfirmasi
import android.app.AlertDialog
// Mengimpor Intent untuk navigasi antar activity
import android.content.Intent
// Mengimpor Bundle untuk menyimpan state fragment
import android.os.Bundle
// Mengimpor Editable untuk manipulasi teks yang bisa diedit
import android.text.Editable
// Mengimpor TextWatcher untuk memantau perubahan pada EditText
import android.text.TextWatcher
// Mengimpor LayoutInflater untuk menginflate layout XML
import android.view.LayoutInflater
// Mengimpor View sebagai komponen dasar UI
import android.view.View
// Mengimpor ViewGroup sebagai container untuk view
import android.view.ViewGroup
// Mengimpor EditText untuk input teks pencarian
import android.widget.EditText
// Mengimpor Fragment sebagai komponen dasar UI Android
import androidx.fragment.app.Fragment
// Mengimpor LinearLayoutManager untuk tata letak linear RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
// Mengimpor RecyclerView untuk menampilkan daftar data yang efisien
import androidx.recyclerview.widget.RecyclerView

// Deklarasi kelas CatatanFragment yang mewarisi Fragment
class CatatanFragment : Fragment() {

    // Deklarasi CatatanPreferences untuk mengelola penyimpanan data catatan
    private lateinit var preferences: CatatanPreferences
    // Deklarasi adapter untuk RecyclerView
    private lateinit var adapter: CatatanAdapter
    // Deklarasi RecyclerView untuk menampilkan daftar catatan
    private lateinit var recyclerView: RecyclerView
    // Deklarasi EditText untuk fitur pencarian
    private lateinit var searchEditText: EditText
    // Variabel untuk menyimpan semua data catatan (tanpa filter)
    private var allCatatan = listOf<Catatan>()

    // Fungsi yang dipanggil saat fragment membuat tampilan UI-nya
    override fun onCreateView(
        inflater: LayoutInflater,       // Objek untuk mengonversi XML ke View
        container: ViewGroup?,          // Container parent untuk fragment
        savedInstanceState: Bundle?     // State yang disimpan sebelumnya
    ): View? {
        // Menginflate layout fragment_catatan.xml menjadi objek View
        return inflater.inflate(R.layout.fragment_catatan, container, false)
    }

    // Fungsi yang dipanggil setelah onCreateView, saat view sudah dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Memanggil implementasi dari kelas induk
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi CatatanPreferences dengan context dari fragment
        preferences = CatatanPreferences(requireContext())

        // Menghubungkan RecyclerView dari layout dengan ID rv_catatan
        recyclerView = view.findViewById(R.id.rv_catatan)

        // Menghubungkan EditText dari layout dengan ID et_search
        searchEditText = view.findViewById(R.id.et_search)

        // Setup RecyclerView dengan adapter dan layout manager
        setupRecyclerView()

        // Setup fitur pencarian
        setupSearch()

        // Memuat data catatan dari penyimpanan
        loadData()
    }

    // Fungsi yang dipanggil saat fragment menjadi aktif/terlihat kembali
    override fun onResume() {
        super.onResume()
        // Memuat ulang data setiap kali fragment kembali aktif
        loadData()
    }

    // Fungsi untuk mengkonfigurasi RecyclerView
    private fun setupRecyclerView() {
        // Membuat instance CatatanAdapter dengan parameter:
        adapter = CatatanAdapter(
            catatanList = emptyList(),  // Data awal kosong
            onItemClick = { catatan ->  // Callback saat item diklik
                // Membuat Intent untuk membuka CatatanActivity (mode lihat detail)
                val intent = Intent(requireContext(), CatatanActivity::class.java).apply {
                    // Menyimpan data catatan ke intent sebagai extra
                    putExtra("id", catatan.id)
                    putExtra("judul", catatan.judul)
                    putExtra("deskripsi", catatan.deskripsi)
                    putExtra("tanggal", catatan.tanggal)
                    putExtra("waktu", catatan.waktu)
                }
                // Memulai activity baru
                startActivity(intent)
            },
            onEditClick = { catatan ->  // Callback saat tombol edit diklik
                // Membuat Intent untuk membuka TambahCatatanActivity (mode edit)
                val intent = Intent(requireContext(), TambahCatatanActivity::class.java).apply {
                    // Menyimpan data catatan ke intent untuk diedit
                    putExtra("id", catatan.id)
                    putExtra("judul", catatan.judul)
                    putExtra("deskripsi", catatan.deskripsi)
                    putExtra("tanggal", catatan.tanggal)
                    putExtra("waktu", catatan.waktu)
                }
                // Memulai activity baru
                startActivity(intent)
            },
            onDeleteClick = { catatan ->  // Callback saat tombol hapus diklik
                // Menampilkan dialog konfirmasi hapus
                showDeleteConfirmation(catatan)
            }
        )

        // Mengatur layout manager RecyclerView menjadi LinearLayoutManager (tampilan vertikal)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Menghubungkan adapter dengan RecyclerView
        recyclerView.adapter = adapter
    }

    // Fungsi untuk mengatur fitur pencarian
    private fun setupSearch() {
        // Menambahkan TextWatcher untuk memantau perubahan teks pada searchEditText
        searchEditText.addTextChangedListener(object : TextWatcher {
            // Dipanggil sebelum teks berubah
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak melakukan apa-apa
            }

            // Dipanggil saat teks berubah
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Melakukan pencarian dengan teks yang baru dimasukkan
                searchCatatan(s.toString())
            }

            // Dipanggil setelah teks berubah
            override fun afterTextChanged(s: Editable?) {
                // Tidak melakukan apa-apa
            }
        })
    }

    // Fungsi untuk memuat data catatan dari penyimpanan
    private fun loadData() {
        // Mengambil semua catatan dan mengurutkan berdasarkan timestamp terbaru
        allCatatan = preferences.getCatatanList().sortedByDescending { it.timestamp }

        // Memperbarui data di adapter dengan semua catatan
        adapter.updateData(allCatatan)
    }

    // Fungsi untuk melakukan pencarian/filtering catatan
    private fun searchCatatan(query: String) {
        // Filter data berdasarkan query pencarian
        val filtered = if (query.isEmpty()) {
            // Jika query kosong, tampilkan semua catatan
            allCatatan
        } else {
            // Jika ada query, filter catatan yang cocok dengan query
            allCatatan.filter { it.querypencocokan(query) }
        }

        // Memperbarui data di adapter dengan hasil filter
        adapter.updateData(filtered)
    }

    // Fungsi untuk menampilkan dialog konfirmasi penghapusan
    private fun showDeleteConfirmation(catatan: Catatan) {
        // Membuat AlertDialog menggunakan Builder
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Catatan")  // Judul dialog
            .setMessage("Apakah Anda yakin ingin menghapus \"${catatan.judul}\"?")  // Pesan konfirmasi
            .setPositiveButton("Hapus") { _, _ ->  // Tombol positif (Hapus)
                // Menghapus catatan dari penyimpanan
                preferences.deleteCatatan(catatan.id)
                // Memuat ulang data setelah penghapusan
                loadData()
            }
            .setNegativeButton("Batal", null)  // Tombol negatif (Batal), tidak melakukan apa-apa
            .show()  // Menampilkan dialog
    }
}