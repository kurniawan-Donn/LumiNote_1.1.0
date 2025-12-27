package com.example.LumiNote

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import java.io.InputStream

class ProfilActivity : AppCompatActivity() {

    // Views
    private lateinit var backButton: ImageView
    private lateinit var btnEdit: Button
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
    private lateinit var layoutLogout: LinearLayout

    private lateinit var userManager: UserManager
    private lateinit var sessionManager: SessionManager

    companion object {
        private const val EDIT_PROFILE_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        try {
            initViews()
            userManager = UserManager(this)
            sessionManager = SessionManager(this)
            loadUserData()
            setupListeners()
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message} 😅", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Profil berhasil diperbarui ✨", Toast.LENGTH_SHORT).show()
            loadUserData()
        }
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
        layoutLogout = findViewById(R.id.layoutLogout)
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

        // Logout
        layoutLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadUserData() {
        try {
            val userId = sessionManager.getUserId()

            if (userId != null) {
                val user = userManager.getUserById(userId)
                if (user != null) {
                    // Update UI
                    tvNama.text = user.nama
                    tvBio.text = user.bio

                    // Load foto profil dari internal storage
                    if (user.fotoProfil.isNotEmpty()) {
                        loadImageFromInternalStorage(user.fotoProfil)
                    } else {
                        imgProfile.setImageResource(R.drawable.ic_person)
                    }
                } else {
                    Toast.makeText(this, "User tidak ditemukan 🧐", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Session tidak valid 🔄", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading data: ${e.message} 😅", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun loadImageFromInternalStorage(filePath: String) {
        try {
            val bitmap = ImageHelper.loadImageFromInternalStorage(filePath)
            if (bitmap != null) {
                // Crop menjadi circular
                val circularBitmap = getCircularBitmap(bitmap)
                imgProfile.setImageBitmap(circularBitmap)
            } else {
                imgProfile.setImageResource(R.drawable.ic_person)
            }
        } catch (e: Exception) {
            imgProfile.setImageResource(R.drawable.ic_person)
            e.printStackTrace()
        }
    }

    private fun loadCircularImageFromUri(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            val inputStream: InputStream? = contentResolver.openInputStream(uri)

            if (inputStream != null) {
                var bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                // Rotate jika perlu (handle rotasi dari EXIF)
                bitmap = rotateImageIfRequired(bitmap, uri)

                // Crop menjadi circular
                val circularBitmap = getCircularBitmap(bitmap)

                imgProfile.setImageBitmap(circularBitmap)
            } else {
                imgProfile.setImageResource(R.drawable.ic_person)
            }
        } catch (e: Exception) {
            imgProfile.setImageResource(R.drawable.ic_person)
            e.printStackTrace()
        }
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, uri: Uri): Bitmap {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val exif = inputStream?.let { ExifInterface(it) }
            inputStream?.close()

            val orientation = exif?.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            ) ?: ExifInterface.ORIENTATION_NORMAL

            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: Exception) {
            return bitmap
        }
    }

    private fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = android.graphics.Canvas(output)
        val paint = android.graphics.Paint()
        val rect = android.graphics.Rect(0, 0, size, size)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(squaredBitmap, rect, rect, paint)

        return output
    }

    private fun openEditProfil() {
        try {
            val intent = Intent(this, EditProfilActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message} 😅", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFaforit() {
        val intent = Intent(this, FaforitActivity::class.java)
        startActivity(intent)
    }

    private fun openArsip() {
        val intent = Intent(this, ArsipActivity::class.java)
        startActivity(intent)
    }

    // ✅ FIXED: Langsung start activity, tidak perlu set listener lagi
    private fun openStatistik() {
        startActivity(Intent(this, StatistikActivity::class.java))
    }

    private fun toggleModeGelap(isEnabled: Boolean) {
        val message = if (isEnabled) "Mode Gelap diaktifkan 🌙✨" else "Mode Terang diaktifkan ☀️✨"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun togglePemberitahuan(isEnabled: Boolean) {
        val message = if (isEnabled) "Pemberitahuan diaktifkan 🔔✅" else "Pemberitahuan dinonaktifkan 🔕❌"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showBahasaBottomSheet() {
        Toast.makeText(this, "Pilih Bahasa akan segera hadir 🌍✨", Toast.LENGTH_SHORT).show()
    }

    private fun openBackupRestore() {
        startActivity(Intent(this, BackupRestoreActivity::class.java))
    }

    // =====================================
// HAPUS DATA - Tambahkan ke ProfilActivity.kt
// =====================================

// Ganti method showHapusDataDialog() yang sudah ada dengan ini:

    private fun showHapusDataDialog() {
        try {
            // Inflate custom dialog layout
            val dialogView = layoutInflater.inflate(R.layout.dialog_hapus_data, null)

            // Create dialog
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // Make dialog background transparent (untuk rounded corners)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

            // Get views from dialog
            val tvTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
            val btnBatal = dialogView.findViewById<Button>(R.id.btnBatal)
            val btnYa = dialogView.findViewById<Button>(R.id.btnYa)
            val ivIllustration = dialogView.findViewById<ImageView>(R.id.ivIllustration)

            // Set title dengan emoji
            tvTitle.text = "Anda Yakin Ingin Menghapus Data Aplikasi? 🗑️"

            // Button Batal - dismiss dialog
            btnBatal.setOnClickListener {
                dialog.dismiss()
                Toast.makeText(this, "Penghapusan dibatalkan 😅", Toast.LENGTH_SHORT).show()
            }

            // Button Ya - hapus data
            btnYa.setOnClickListener {
                dialog.dismiss()
                hapusSemuaData()
            }

            // Show dialog
            dialog.show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error dialog: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // Method untuk hapus semua data dengan tracking detail
    private fun hapusSemuaData() {
        try {
            // Ambil jumlah data SEBELUM dihapus
            val catatanPrefs = CatatanPreferences(this)
            val jumlahCatatan = catatanPrefs.getCatatanList().size

            val tugasPrefs = TugasPreferences(this)
            val jumlahTugas = tugasPrefs.getAllTugas().size

            val arsipPrefs = ArsipPreferences(this)
            val jumlahArsipCatatan = arsipPrefs.getArsipCatatan().size
            val jumlahArsipTugas = arsipPrefs.getArsipTugas().size

            // HAPUS DATA
            catatanPrefs.saveCatatanList(emptyList())
            tugasPrefs.saveList(emptyList())
            arsipPrefs.saveArsipCatatan(emptyList())
            arsipPrefs.saveArsipTugas(emptyList())

            // Hapus info backup terakhir
            //  val backupPrefs = BackupPreferences(this)
            //  backupPrefs.saveLastBackupDate("")

            // Hitung total
            val totalDihapus = jumlahCatatan + jumlahTugas + jumlahArsipCatatan + jumlahArsipTugas

            // Tampilkan hasil sesuai yang terhapus
            showHapusDataSuccessDialog(
                total = totalDihapus,
                catatan = jumlahCatatan,
                tugas = jumlahTugas,
                arsipCatatan = jumlahArsipCatatan,
                arsipTugas = jumlahArsipTugas
            )

        } catch (e: Exception) {
            Toast.makeText(this, "Gagal menghapus data: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // Dialog konfirmasi berhasil dengan detail dinamis
    private fun showHapusDataSuccessDialog(
        total: Int,
        catatan: Int,
        tugas: Int,
        arsipCatatan: Int,
        arsipTugas: Int
    ) {
        // Jika tidak ada data yang dihapus
        if (total == 0) {
            AlertDialog.Builder(this)
                .setTitle("Tidak Ada Data 🤔")
                .setMessage("Tidak ada data yang perlu dihapus.\nAplikasi sudah bersih! ✨")
                .setPositiveButton("Oke! 👍") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
            return
        }

        // Build pesan dinamis berdasarkan apa yang terhapus
        val message = buildString {
            append("Total $total item telah dihapus:\n\n")

            if (catatan > 0) {
                append("• $catatan Catatan\n")
            }

            if (tugas > 0) {
                append("• $tugas Tugas\n")
            }

            if (arsipCatatan > 0) {
                append("• $arsipCatatan Arsip Catatan\n")
            }

            if (arsipTugas > 0) {
                append("• $arsipTugas Arsip Tugas\n")
            }

            append("\nAplikasi siap untuk digunakan kembali! 🎉")
        }

        AlertDialog.Builder(this)
            .setTitle("Data Berhasil Dihapus! 🗑️✨")
            .setMessage(message)
            .setPositiveButton("Oke! 👍") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }



    private fun openTentangKami() {
        startActivity(Intent(this, TentangKamiActivity::class.java))
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout 🚪😊")
            .setMessage("Apakah Anda yakin ingin logout? 🤔\nJangan lupa kembali ya! 👋")
            .setPositiveButton("Logout 👋") { dialog, _ ->
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Tetap di Sini 😊") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        // Hapus session
        sessionManager.logout()

        Toast.makeText(this, "Berhasil logout 👋✨", Toast.LENGTH_SHORT).show()

        // Redirect ke LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}