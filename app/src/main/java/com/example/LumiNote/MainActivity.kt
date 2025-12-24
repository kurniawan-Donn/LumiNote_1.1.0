package com.example.LumiNote

// Mengimpor kelas Bundle untuk menyimpan dan mengembalikan state aktivitas
import android.os.Bundle
// Mengimpor TextView untuk menampilkan teks di antarmuka pengguna
import android.widget.TextView
// Mengimpor callback untuk menangani perilaku tombol back secara kustom
import androidx.activity.OnBackPressedCallback
// Mengimpor AppCompatActivity sebagai kelas dasar untuk kompatibilitas
import androidx.appcompat.app.AppCompatActivity
// Mengimpor NavHostFragment untuk mengelola navigasi antar fragment
import androidx.navigation.fragment.NavHostFragment
// Mengimpor fungsi ekstensi untuk menghubungkan BottomNavigationView dengan NavController
import androidx.navigation.ui.setupWithNavController
// Mengimpor BottomNavigationView untuk menu navigasi bawah
import com.google.android.material.bottomnavigation.BottomNavigationView
// Mengimpor FloatingActionButton untuk tombol aksi mengambang
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Deklarasi kelas MainActivity yang mewarisi AppCompatActivity
class MainActivity : AppCompatActivity() {

    // Deklarasi variabel untuk BottomNavigationView dengan inisialisasi tunda (lateinit)
    private lateinit var bottomNav: BottomNavigationView
    // Deklarasi variabel untuk FloatingActionButton dengan inisialisasi tunda
    private lateinit var fab: FloatingActionButton
    // Deklarasi variabel untuk TextView header dengan inisialisasi tunda
    private lateinit var mainHeader: TextView

    // Override fungsi onCreate yang dipanggil saat aktivitas dibuat
    override fun onCreate(savedInstanceState: Bundle?) {
        // Memanggil implementasi onCreate dari kelas induk
        super.onCreate(savedInstanceState)
        // Menetapkan layout aktivitas dari file XML activity_main
        setContentView(R.layout.activity_main)

        // Inisialisasi view BottomNavigationView dari layout dengan ID bottom_nav_view
        bottomNav = findViewById(R.id.bottom_nav_view)
        // Inisialisasi view FloatingActionButton dari layout dengan ID fab_add
        fab = findViewById(R.id.fab_add)
        // Inisialisasi view TextView dari layout dengan ID main_header
        mainHeader = findViewById(R.id.main_header)

        // Memanggil fungsi untuk menyiapkan navigasi antar fragment
        setupNavigation()

        // Memanggil fungsi untuk menyiapkan aksi klik pada FloatingActionButton
        setupFab()

        // Memanggil fungsi untuk menyiapkan listener BottomNavigationView untuk update header
        setupBottomNavListener()

        // Memanggil fungsi untuk menangani perilaku tombol back
        setupBackPressHandler()
    }

    // Fungsi pribadi untuk menyiapkan navigasi menggunakan Navigation Component
    private fun setupNavigation() {
        // Mendapatkan NavHostFragment dari FragmentManager dengan ID catatan_fragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.catatan_fragment) as NavHostFragment
        // Mendapatkan NavController dari NavHostFragment untuk mengelola navigasi
        val navController = navHostFragment.navController

        // Menghubungkan BottomNavigationView dengan NavController untuk navigasi otomatis
        bottomNav.setupWithNavController(navController) //menghubungkan navigasi bawah dengan sistem pada navigasi fragmen

        // Menetapkan listener untuk item yang dipilih di BottomNavigationView
        bottomNav.setOnItemSelectedListener { item ->
            // When expression untuk menangani berdasarkan ID item yang diklik
            when (item.itemId) {
                // Jika item Catatan diklik
                R.id.navigation_catatan -> {
                    // Navigasi ke fragment Catatan menggunakan NavController
                    navController.navigate(R.id.navigation_catatan)
                    // Mengupdate teks header menjadi "Daftar Catatan"
                    mainHeader.text = getString(R.string.daftar_catatan)
                    // Mengembalikan true untuk menandakan item berhasil dipilih
                    true
                }
                // Jika item Tugas diklik
                R.id.navigation_tugas -> {
                    // Navigasi ke fragment Tugas menggunakan NavController
                    navController.navigate(R.id.navigation_tugas)
                    // Mengupdate teks header menjadi "Daftar Tugas"
                    mainHeader.text = getString(R.string.daftar_tugas)
                    // Mengembalikan true untuk menandakan item berhasil dipilih
                    true
                }
                // Jika item Plus (tengah) diklik
                R.id.navigation_plus -> {
                    // Tombol tengah - membuka dialog sama seperti FAB
                    showPilihTipeDialog()
                    // Mengembalikan false karena tidak melakukan navigasi fragment
                    false // Don't navigate
                }
                // Untuk item lainnya, kembalikan false
                else -> false
            }
        }
    }

    // Fungsi pribadi untuk menyiapkan aksi klik pada FloatingActionButton
    private fun setupFab() {
        // Menetapkan listener klik pada FAB untuk menampilkan dialog pilihan tipe
        fab.setOnClickListener {
            showPilihTipeDialog()
        }
    }

    // Fungsi pribadi untuk menyiapkan listener ketika item BottomNavigationView dipilih kembali (reselected)
    private fun setupBottomNavListener() {
        // Update teks header berdasarkan item yang dipilih kembali
        bottomNav.setOnItemReselectedListener { item ->
            when (item.itemId) {
                // Jika item Catatan dipilih kembali, update header
                R.id.navigation_catatan -> mainHeader.text = getString(R.string.daftar_catatan)
                // Jika item Tugas dipilih kembali, update header
                R.id.navigation_tugas -> mainHeader.text = getString(R.string.daftar_tugas)
            }
        }
    }

    // Fungsi pribadi untuk menampilkan BottomSheetDialog untuk memilih tipe (Catatan/Tugas)
    private fun showPilihTipeDialog() {
        // Membuat instance BottomSheetDialog (asumsi kelas BottomSheetDialog sudah didefinisikan)
        val bottomSheet = BottomSheetDialog()
        // Menampilkan BottomSheetDialog dengan tag "PilihTipeBottomSheet"
        bottomSheet.show(supportFragmentManager, "PilihTipeBottomSheet")
    }

    // Fungsi pribadi untuk menangani perilaku tombol back secara kustom
    private fun setupBackPressHandler() {
        // Menambahkan callback ke OnBackPressedDispatcher untuk menimpa perilaku default tombol back
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            // Override fungsi handleOnBackPressed yang dipanggil saat tombol back ditekan
            override fun handleOnBackPressed() {
                // Mendapatkan NavHostFragment dan NavController (sama seperti di setupNavigation)
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.catatan_fragment) as NavHostFragment
                val navController = navHostFragment.navController

                // Mengecek apakah pengguna sedang berada di fragment utama (Catatan atau Tugas)
                if (navController.currentDestination?.id == R.id.navigation_catatan ||
                    navController.currentDestination?.id == R.id.navigation_tugas) {
                    // Jika di fragment utama, tutup aplikasi (finish activity)
                    finish()
                } else {
                    // Jika tidak di fragment utama, lakukan navigasi kembali ke fragment sebelumnya
                    navController.navigateUp()
                }
            }
        })
    }
}