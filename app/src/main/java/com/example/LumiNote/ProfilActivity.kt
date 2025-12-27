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
            Toast.makeText(this, "Error: ${e.message} 😅", Toast.LENGTH_LONG).show() // ✅ DITAMBAHKAN EMOJI
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Profil berhasil diperbarui ✨", Toast.LENGTH_SHORT).show() // ✅ DITAMBAHKAN EMOJI
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
                    Toast.makeText(this, "User tidak ditemukan 🧐", Toast.LENGTH_SHORT).show() // ✅ DITAMBAHKAN EMOJI
                }
            } else {
                Toast.makeText(this, "Session tidak valid 🔄", Toast.LENGTH_SHORT).show() // ✅ DITAMBAHKAN EMOJI
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading data: ${e.message} 😅", Toast.LENGTH_LONG).show() // ✅ DITAMBAHKAN EMOJI
            e.printStackTrace()
        }
    }

    // ✅ Load image dari internal storage (file path)
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

    // ✅ Fungsi native untuk load dan crop circular image dari URI (untuk preview)
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

    // Fungsi untuk rotate image sesuai EXIF
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

    // Fungsi untuk membuat circular bitmap
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
            Toast.makeText(this, "Error: ${e.message} 😅", Toast.LENGTH_SHORT).show() // ✅ DITAMBAHKAN EMOJI
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

    private fun openStatistik() {
        startActivity(Intent(this, StatistikActivity::class.java))
    }

    private fun toggleModeGelap(isEnabled: Boolean) {
        val message = if (isEnabled) "Mode Gelap diaktifkan 🌙✨" else "Mode Terang diaktifkan ☀️✨" // ✅ DITAMBAHKAN EMOJI
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun togglePemberitahuan(isEnabled: Boolean) {
        val message = if (isEnabled) "Pemberitahuan diaktifkan 🔔✅" else "Pemberitahuan dinonaktifkan 🔕❌" // ✅ DITAMBAHKAN EMOJI
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showBahasaBottomSheet() {
        Toast.makeText(this, "Pilih Bahasa akan segera hadir 🌍✨", Toast.LENGTH_SHORT).show() // ✅ DITAMBAHKAN EMOJI
    }

    private fun openBackupRestore() {
        startActivity(Intent(this, BackupRestoreActivity::class.java))
    }

    private fun showHapusDataDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Data 🗑️😱") // ✅ DITAMBAHKAN EMOJI
            .setMessage("Apakah Anda yakin ingin menghapus semua data? 🤔\nTindakan ini tidak dapat dibatalkan! ⚠️") // ✅ DITAMBAHKAN EMOJI
            .setPositiveButton("Hapus Semua 🔥") { dialog, _ -> // ✅ DITAMBAHKAN EMOJI
                hapusSemuaData()
                dialog.dismiss()
            }
            .setNegativeButton("Jangan! 😅") { dialog, _ -> // ✅ DITAMBAHKAN EMOJI
                dialog.dismiss()
            }
            .show()
    }

    private fun hapusSemuaData() {
        // Implementasi hapus data di sini
        Toast.makeText(this, "Semua data telah dihapus 🗑️✨", Toast.LENGTH_SHORT).show() // ✅ DITAMBAHKAN EMOJI
    }

    private fun openTentangKami() {
        startActivity(Intent(this, TentangKamiActivity::class.java))
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout 🚪😊") // ✅ DITAMBAHKAN EMOJI
            .setMessage("Apakah Anda yakin ingin logout? 🤔\nJangan lupa kembali ya! 👋") // ✅ DITAMBAHKAN EMOJI
            .setPositiveButton("Logout 👋") { dialog, _ -> // ✅ DITAMBAHKAN EMOJI
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Tetap di Sini 😊") { dialog, _ -> // ✅ DITAMBAHKAN EMOJI
                dialog.dismiss()
            }
            .show()
    }

    private fun logout() {
        // Hapus session
        sessionManager.logout()

        Toast.makeText(this, "Berhasil logout 👋✨", Toast.LENGTH_SHORT).show() // ✅ DITAMBAHKAN EMOJI

        // Redirect ke LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}