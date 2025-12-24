package com.example.LumiNote  // Mendeklarasikan package/namespace untuk aplikasi

// ===============================
// IMPORTS
// ===============================
import android.content.Intent  // Untuk berpindah antar activity
import android.os.Bundle  // Untuk menyimpan state fragment
import android.text.Editable  // Interface untuk text yang bisa diedit
import android.text.TextWatcher  // Listener untuk perubahan text
import android.view.LayoutInflater  // Untuk mengubah XML layout menjadi View object
import android.view.View  // Class dasar untuk komponen UI
import android.view.ViewGroup  // Container untuk kumpulan View
import android.view.animation.AccelerateDecelerateInterpolator  // Animasi dengan percepatan lalu perlambatan
import android.widget.EditText  // Komponen input teks
import android.widget.ImageView  // Komponen untuk menampilkan gambar/icon
import android.widget.LinearLayout  // Layout linear untuk mengatur komponen
import android.widget.TextView  // Komponen untuk menampilkan teks
import androidx.core.view.isVisible  // Extension function untuk mengecek visibility
import androidx.fragment.app.Fragment  // Base class untuk fragment
import androidx.recyclerview.widget.LinearLayoutManager  // Layout manager untuk RecyclerView
import androidx.recyclerview.widget.RecyclerView  // Komponen untuk menampilkan list yang bisa di-scroll

// ===============================
// CLASS TUGAS FRAGMENT
// Fragment untuk menampilkan daftar tugas (baik yang belum dan sudah selesai)
// ===============================
class TugasFragment : Fragment() {  // Mendefinisikan fragment untuk menampilkan tugas

    // ===============================
    // DEKLARASI VIEW COMPONENTS
    // ===============================
    private lateinit var layoutTugasSelesai: LinearLayout  // Layout header untuk tugas selesai (bisa diklik)
    private lateinit var icDiselesaikan: ImageView  // Icon panah untuk expand/collapse
    private lateinit var tvDiselesaikan: TextView  // Text untuk judul "Diselesaikan"
    private lateinit var rvTugasSelesai: RecyclerView  // RecyclerView untuk menampilkan tugas yang sudah selesai
    private lateinit var tugasSelesaiAdapter: TugasAdapter  // Adapter untuk tugas selesai
    private lateinit var rvTugas: RecyclerView  // RecyclerView untuk menampilkan tugas yang belum selesai
    private lateinit var etSearch: EditText  // Input untuk pencarian tugas
    private lateinit var tugasPreferences: TugasPreferences  // Untuk menyimpan dan mengambil data tugas
    private lateinit var tugasAdapter: TugasAdapter  // Adapter untuk tugas belum selesai

    // ===============================
    // VARIABEL STATE
    // ===============================
    private var tampilkanTugasSelesai = true  // Flag untuk status expand/collapse tugas selesai

    // ===============================
    // METHOD onCreateView
    // Dipanggil saat fragment membuat tampilan UI-nya
    // ===============================
    override fun onCreateView(
        inflater: LayoutInflater,  // Objek untuk meng-inflate layout
        container: ViewGroup?,  // Container parent yang akan menjadi host fragment
        savedInstanceState: Bundle?  // State yang disimpan sebelumnya
    ): View {
        // Meng-inflate layout fragment_tugas.xml menjadi View
        return inflater.inflate(R.layout.fragment_tugas, container, false)
    }

    // ===============================
    // METHOD onViewCreated
    // Dipanggil setelah onCreateView, untuk menginisialisasi view
    // ===============================
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)  // Memanggil implementasi parent class

        // Inisialisasi tugasPreferences untuk mengakses penyimpanan data
        tugasPreferences = TugasPreferences(requireContext())

        // Inisialisasi view dengan komponen di layout
        layoutTugasSelesai = view.findViewById(R.id.layout_tugas_selesai)
        icDiselesaikan = view.findViewById(R.id.ic_diselesaikan)
        tvDiselesaikan = view.findViewById(R.id.tv_diselesaikan)
        rvTugasSelesai = view.findViewById(R.id.rv_tugas_selesai)
        rvTugas = view.findViewById(R.id.rv_tugas)
        etSearch = view.findViewById(R.id.et_search)

        // Setup berbagai komponen
        setupSearch()  // Mengatur fitur pencarian
        setupRecyclerView()  // Mengatur RecyclerView untuk tugas belum selesai
        setupRecyclerViewSelesai()  // Mengatur RecyclerView untuk tugas selesai
        setupClickListeners()  // Mengatur listener klik
        loadData()  // Memuat data dari penyimpanan

        // Tampilkan RecyclerView tugas selesai langsung
        animasiTugasSelesai(tampilkanTugasSelesai)  // Animasi untuk menampilkan/menyembunyikan
        updateCompletedText(tampilkanTugasSelesai)  // Update teks "Diselesaikan"
    }

    // ===============================
    // METHOD setupRecyclerView
    // Mengatur RecyclerView untuk menampilkan tugas yang BELUM selesai
    // ===============================
    private fun setupRecyclerView() {
        // Membuat adapter untuk tugas belum selesai dengan callback functions
        tugasAdapter = TugasAdapter(
            listTugas = mutableListOf(),  // List kosong sementara
            onEditClick = { tugas -> openEditTugas(tugas) },  // Callback saat edit diklik
            onDeleteClick = { tugas -> deleteTugas(tugas) },  // Callback saat hapus diklik
            onCheckedChange = { tugas, isChecked -> updateStatusTugas(tugas, isChecked) }  // Callback saat checkbox berubah
        )

        // Mengatur RecyclerView
        rvTugas.apply {
            layoutManager = LinearLayoutManager(requireContext())  // Mengatur layout manager
            adapter = tugasAdapter  // Mengatur adapter
        }
    }

    // ===============================
    // METHOD setupRecyclerViewSelesai
    // Mengatur RecyclerView untuk menampilkan tugas yang SUDAH selesai
    // ===============================
    private fun setupRecyclerViewSelesai() {
        // Membuat adapter untuk tugas selesai dengan callback functions
        tugasSelesaiAdapter = TugasAdapter(
            listTugas = mutableListOf(),  // List kosong sementara
            onEditClick = { tugas -> openEditTugas(tugas) },  // Callback saat edit diklik
            onDeleteClick = { tugas -> deleteTugas(tugas) },  // Callback saat hapus diklik
            onCheckedChange = { tugas, isChecked -> updateStatusTugas(tugas, isChecked) }  // Callback saat checkbox berubah
        )

        // Mengatur RecyclerView
        rvTugasSelesai.apply {
            layoutManager = LinearLayoutManager(requireContext())  // Mengatur layout manager
            adapter = tugasSelesaiAdapter  // Mengatur adapter
        }
    }

    // ===============================
    // METHOD setupClickListeners
    // Mengatur listener klik untuk komponen
    // ===============================
    private fun setupClickListeners() {
        // Listener untuk layout tugas selesai (untuk expand/collapse)
        layoutTugasSelesai.setOnClickListener {
            tampilkanTugasSelesai = !tampilkanTugasSelesai  // Toggle status expand/collapse
            animasiTugasSelesai(tampilkanTugasSelesai)  // Jalankan animasi
            updateCompletedText(tampilkanTugasSelesai)  // Update teks
        }
    }

    // ===============================
    // METHOD animasiTugasSelesai
    // Menangani animasi expand/collapse untuk bagian tugas selesai
    // ===============================
    private fun animasiTugasSelesai(ditunjukkan: Boolean) {
        // Animasi rotasi icon panah
        icDiselesaikan.animate()
            .rotation(if (ditunjukkan) 180f else 0f)  // Rotasi 180 derajat jika ditunjukkan
            .setDuration(300)  // Durasi 300ms
            .setInterpolator(AccelerateDecelerateInterpolator())  // Interpolator percepatan-perlambatan
            .start()

        // Animasi untuk RecyclerView tugas selesai
        if (ditunjukkan) {
            // Jika ditunjukkan, fade in dari transparan
            rvTugasSelesai.apply {
                alpha = 0f  // Mulai dari transparan
                isVisible = true  // Tampilkan view
                animate().alpha(1f).setDuration(300).start()  // Animasi ke opacity penuh
            }
        } else {
            // Jika disembunyikan, fade out lalu sembunyikan
            rvTugasSelesai.animate()
                .alpha(0f)  // Animasi ke transparan
                .setDuration(200)  // Durasi 200ms
                .withEndAction { rvTugasSelesai.isVisible = false }  // Setelah animasi, sembunyikan view
                .start()
        }
    }

    // ===============================
    // METHOD updateCompletedText
    // Mengupdate teks "Diselesaikan" dengan jumlah tugas selesai
    // ===============================
    private fun updateCompletedText(isExpanded: Boolean) {
        val completedCount = tugasSelesaiAdapter.itemCount  // Mendapatkan jumlah tugas selesai
        tvDiselesaikan.text = if (isExpanded) "Diselesaikan ($completedCount)" else "Diselesaikan"
        // Jika expanded, tampilkan dengan jumlah, jika tidak hanya "Diselesaikan"
    }

    // ===============================
    // METHOD loadData
    // Memuat data dari penyimpanan dan mengelompokkan berdasarkan status selesai
    // ===============================
    private fun loadData() {
        val allTugas = tugasPreferences.getAllTugas()  // Mendapatkan semua tugas dari penyimpanan
        val belumSelesai = allTugas.filter { !it.isSelesai }  // Filter tugas belum selesai
        val selesai = allTugas.filter { it.isSelesai }  // Filter tugas sudah selesai

        // Update kedua adapter dengan data yang sesuai
        tugasAdapter.updateData(belumSelesai)
        tugasSelesaiAdapter.updateData(selesai)

        updateCompletedText(tampilkanTugasSelesai)  // Update teks dengan jumlah terbaru
    }

    // ===============================
    // METHOD setupSearch
    // Mengatur fitur pencarian real-time
    // ===============================
    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            // Dipanggil sebelum teks berubah
            override fun beforeTextChanged(inputteks: CharSequence?, start: Int, count: Int, after: Int) {}

            // Dipanggil saat teks berubah
            override fun onTextChanged(inputteks: CharSequence?, start: Int, before: Int, count: Int) {
                val query = inputteks.toString().lowercase()  // Mengubah query menjadi lowercase
                val allTugas = tugasPreferences.getAllTugas()  // Mendapatkan semua tugas

                // Filter tugas berdasarkan query dan status
                val belumSelesaiList = allTugas.filter {
                    !it.isSelesai && it.matchesQuery(query)  // Belum selesai DAN cocok dengan query
                }
                val selesaiList = allTugas.filter {
                    it.isSelesai && it.matchesQuery(query)  // Sudah selesai DAN cocok dengan query
                }

                // Update adapter dengan hasil filter
                tugasAdapter.updateData(belumSelesaiList)
                tugasSelesaiAdapter.updateData(selesaiList)

                // Tampilkan/sembunyikan RecyclerView tugas selesai berdasarkan apakah ada hasil
                rvTugasSelesai.isVisible = selesaiList.isNotEmpty()
                updateCompletedText(tampilkanTugasSelesai)  // Update teks
            }

            // Dipanggil setelah teks berubah
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // ===============================
    // METHOD openEditTugas
    // Membuka activity untuk mengedit tugas
    // ===============================
    private fun openEditTugas(tugas: Tugas) {
        // Membuat intent untuk pindah ke TambahTugasActivity
        val intent = Intent(requireContext(), TambahTugasActivity::class.java).apply {
            putExtra("id", tugas.id)  // Mengirim ID tugas
            putExtra("judul", tugas.judul)  // Mengirim judul
            putExtra("deskripsi", tugas.deskripsi)  // Mengirim deskripsi
            putExtra("tanggal", tugas.tanggal)  // Mengirim tanggal
            putExtra("isSelesai", tugas.isSelesai)  // Mengirim status selesai
        }
        startActivity(intent)  // Memulai activity
    }

    // ===============================
    // METHOD deleteTugas
    // Menghapus tugas dengan konfirmasi dialog
    // ===============================
    private fun deleteTugas(tugas: Tugas) {
        // Membuat AlertDialog untuk konfirmasi penghapusan
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Hapus Tugas")  // Judul dialog
            .setMessage("Apakah Anda yakin ingin menghapus \"${tugas.judul}\"?")  // Pesan dengan judul tugas
            .setPositiveButton("Hapus") { _, _ ->  // Tombol Hapus
                tugasPreferences.deleteTugas(tugas.id)  // Menghapus tugas dari penyimpanan
                loadData()  // Memuat ulang data
            }
            .setNegativeButton("Batal", null)  // Tombol Batal
            .show()  // Menampilkan dialog
    }

    // ===============================
    // METHOD updateStatusTugas
    // Mengupdate status selesai/belum selesai dari tugas
    // ===============================
    private fun updateStatusTugas(tugas: Tugas, isChecked: Boolean) {
        val updatedTugas = tugas.copy(isSelesai = isChecked)  // Membuat copy tugas dengan status baru
        tugasPreferences.updateTugas(updatedTugas)  // Menyimpan perubahan ke penyimpanan
        loadData()  // Memuat ulang data untuk menampilkan perubahan
    }

    // ===============================
    // METHOD onResume
    // Dipanggil saat fragment menjadi visible kembali
    // ===============================
    override fun onResume() {
        super.onResume()
        loadData()  // Memuat ulang data saat fragment kembali aktif
    }
}