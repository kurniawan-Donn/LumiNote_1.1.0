package com.example.LumiNote

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton

class ProfilActivity : AppCompatActivity() {

    // Views
    private lateinit var backButton: ImageView
    private lateinit var btnEdit: MaterialButton
    private lateinit var imgProfile: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvBio: TextView

    // Menu Items
    private lateinit var layoutFsforit: LinearLayout
    private lateinit var layoutArsip: LinearLayout
    private lateinit var layoutStatistik: LinearLayout

    // Pengaturan Items
    private lateinit var switchModeGelap: SwitchCompat
    private lateinit var switchPemberitahuan: SwitchCompat
    private lateinit var layoutBahasa: LinearLayout
    private lateinit var tvBahasa: TextView

    // Tentang Aplikasi Items
    private lateinit var layoutBackup: LinearLayout
    private lateinit var layoutHapusData: LinearLayout
    private lateinit var layoutTentangKami: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        initViews()
        setupListeners()
        loadUserData()
    }

    private fun initViews() {
        // Header
        backButton = findViewById(R.id.backButton)
        btnEdit = findViewById(R.id.btnEdit)

        // Profile Info
        imgProfile = findViewById(R.id.imgProfile)
        tvNama = findViewById(R.id.tvNama)
        tvBio = findViewById(R.id.tvBio)

        // Menu
        layoutFsforit = findViewById(R.id.layoutFsforit)
        layoutArsip = findViewById(R.id.layoutArsip)
        layoutStatistik = findViewById(R.id.layoutStatistik)

        // Pengaturan
        switchModeGelap = findViewById(R.id.switchModeGelap)
        switchPemberitahuan = findViewById(R.id.switchPemberitahuan)
        layoutBahasa = findViewById(R.id.layoutBahasa)
        tvBahasa = findViewById(R.id.tvBahasa)

        // Tentang Aplikasi
        layoutBackup = findViewById(R.id.layoutBackup)
        layoutHapusData = findViewById(R.id.layoutHapusData)
        layoutTentangKami = findViewById(R.id.layoutTentangKami)
    }

    private fun setupListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Edit button
        btnEdit.setOnClickListener {
            openEditProfil()
        }

        // Menu listeners
        layoutFsforit.setOnClickListener {
            openFaforit()
        }

        layoutArsip.setOnClickListener {
            openArsip()
        }

        layoutStatistik.setOnClickListener {
            openStatistik()
        }

        // Pengaturan listeners
        switchModeGelap.setOnCheckedChangeListener { _, isChecked ->
            toggleModeGelap(isChecked)
        }

        switchPemberitahuan.setOnCheckedChangeListener { _, isChecked ->
            togglePemberitahuan(isChecked)
        }

        layoutBahasa.setOnClickListener {
            showBahasaBottomSheet()
        }

        // Tentang Aplikasi listeners
        layoutBackup.setOnClickListener {
            openBackupRestore()
        }

        layoutHapusData.setOnClickListener {
            showHapusDataDialog()
        }

        layoutTentangKami.setOnClickListener {
            openTentangKami()
        }
    }

    private fun loadUserData() {
        // TODO: Load data dari SharedPreferences atau database
        // Sementara menggunakan data default
        tvNama.text = "Someone"
        tvBio.text = "Deskripsi Bio..."
    }

    private fun openEditProfil() {
        // TODO: Buka EditProfilActivity
        Toast.makeText(this, "Edit Profil akan segera hadir", Toast.LENGTH_SHORT).show()
        // val intent = Intent(this, EditProfilActivity::class.java)
        // startActivity(intent)
    }

    private fun openFaforit() {
        // TODO: Buka FaforitActivity
        Toast.makeText(this, "Faforit akan segera hadir", Toast.LENGTH_SHORT).show()
        // val intent = Intent(this, FaforitActivity::class.java)
        // startActivity(intent)
    }

    private fun openArsip() {
        // TODO: Buka ArsipActivity
        Toast.makeText(this, "Arsip akan segera hadir", Toast.LENGTH_SHORT).show()
        // val intent = Intent(this, ArsipActivity::class.java)
        // startActivity(intent)
    }

    private fun openStatistik() {
        // TODO: Buka StatistikActivity
        Toast.makeText(this, "Statistik akan segera hadir", Toast.LENGTH_SHORT).show()
        // val intent = Intent(this, StatistikActivity::class.java)
        // startActivity(intent)
    }

    private fun toggleModeGelap(isEnabled: Boolean) {
        // TODO: Implementasi mode gelap
        val message = if (isEnabled) "Mode Gelap diaktifkan" else "Mode Gelap dinonaktifkan"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // Simpan preferensi
        // SharedPreferences logic here
    }

    private fun togglePemberitahuan(isEnabled: Boolean) {
        // TODO: Implementasi notifikasi
        val message = if (isEnabled) "Pemberitahuan diaktifkan" else "Pemberitahuan dinonaktifkan"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        // Simpan preferensi
        // SharedPreferences logic here
    }

    private fun showBahasaBottomSheet() {
        // TODO: Show bottom sheet untuk pilih bahasa
        Toast.makeText(this, "Pilih Bahasa akan segera hadir", Toast.LENGTH_SHORT).show()
        // val bottomSheet = BahasaBottomSheet()
        // bottomSheet.show(supportFragmentManager, "BahasaBottomSheet")
    }

    private fun openBackupRestore() {
        // TODO: Buka BackupRestoreActivity
        Toast.makeText(this, "Backup & Restore akan segera hadir", Toast.LENGTH_SHORT).show()
        // val intent = Intent(this, BackupRestoreActivity::class.java)
        // startActivity(intent)
    }

    private fun showHapusDataDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data")
            .setMessage("Apakah Anda yakin ingin menghapus semua data? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { dialog, _ ->
                hapusSemuaData()
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun hapusSemuaData() {
        // TODO: Hapus semua data dari database dan preferences
        Toast.makeText(this, "Semua data telah dihapus", Toast.LENGTH_SHORT).show()

        // Clear SharedPreferences
        // Clear Database
        // Reset to default values
    }

    private fun openTentangKami() {
        // TODO: Buka TentangKamiActivity
        Toast.makeText(this, "Tentang Kami akan segera hadir", Toast.LENGTH_SHORT).show()
        // val intent = Intent(this, TentangKamiActivity::class.java)
        // startActivity(intent)
    }
}