package com.example.LumiNote

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class BackupRestoreActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var tvSimpanTerakhir: TextView
    private lateinit var btnCadangkan: Button
    private lateinit var progressBackup: ProgressBar
    private lateinit var btnPulihkan: Button
    private lateinit var tvWarningRestore: TextView

    private lateinit var catatanPrefs: CatatanPreferences
    private lateinit var tugasPrefs: TugasPreferences
    private lateinit var arsipPrefs: ArsipPreferences
    private lateinit var userManager: UserManager
    private lateinit var sessionManager: SessionManager
    private lateinit var backupPrefs: BackupPreferences

    private val gson = Gson()

    // Launcher untuk save file
    private val createFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { saveBackupToFile(it) }
    }

    // Launcher untuk open file
    private val openFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { showRestoreDialog(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_restore)

        // Inisialisasi
        initViews()
        initPreferences()
        loadLastBackupInfo()

        // Setup listeners
        backButton.setOnClickListener { finish() }
        btnCadangkan.setOnClickListener { startBackup() }
        btnPulihkan.setOnClickListener { startRestore() }
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        tvSimpanTerakhir = findViewById(R.id.tvSimpanTerakhir)
        btnCadangkan = findViewById(R.id.btnCadangkan)
        progressBackup = findViewById(R.id.progressBackup)
        btnPulihkan = findViewById(R.id.btnPulihkan)
        tvWarningRestore = findViewById(R.id.tvWarningRestore)
    }

    private fun initPreferences() {
        catatanPrefs = CatatanPreferences(this)
        tugasPrefs = TugasPreferences(this)
        arsipPrefs = ArsipPreferences(this)
        userManager = UserManager(this)
        sessionManager = SessionManager(this)
        backupPrefs = BackupPreferences(this)
    }

    private fun loadLastBackupInfo() {
        val lastBackup = backupPrefs.getLastBackupDate()
        tvSimpanTerakhir.text = if (lastBackup != null) {
            "Simpan Terakhir : $lastBackup"
        } else {
            "Simpan Terakhir : Belum Ada"
        }
    }

    // =====================================
    // BACKUP
    // =====================================

    private fun startBackup() {
        val currentUserId = sessionManager.getUserId()
        if (currentUserId == null) {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate filename dengan timestamp
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "LumiNote_Backup_$timestamp.json"

        // Launch file picker untuk save
        createFileLauncher.launch(filename)
    }

    private fun saveBackupToFile(uri: Uri) {
        progressBackup.visibility = View.VISIBLE
        btnCadangkan.isEnabled = false

        try {
            // Kumpulkan semua data
            val backupData = createBackupData()

            // Konversi ke JSON
            val jsonString = gson.toJson(backupData)

            // Tulis ke file
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(jsonString)
                    writer.flush()
                }
            }

            // Simpan info backup terakhir
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val currentDate = dateFormat.format(Date())
            backupPrefs.saveLastBackupDate(currentDate)

            // Update UI
            loadLastBackupInfo()
            Toast.makeText(this, "Backup berhasil disimpan!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Backup gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            progressBackup.visibility = View.GONE
            btnCadangkan.isEnabled = true
        }
    }

    private fun createBackupData(): BackupData {
        val currentUserId = sessionManager.getUserId() ?: ""
        val currentUser = userManager.getUserById(currentUserId)

        return BackupData(
            version = "1.0",
            backupDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            user = currentUser,
            catatan = catatanPrefs.getCatatanList(),
            tugas = tugasPrefs.getAllTugas(),
            arsipCatatanIds = arsipPrefs.getArsipCatatan(),
            arsipTugasIds = arsipPrefs.getArsipTugas()
        )
    }

    // =====================================
    // RESTORE
    // =====================================

    private fun startRestore() {
        // Launch file picker untuk open
        openFileLauncher.launch(arrayOf("application/json"))
    }

    private fun showRestoreDialog(uri: Uri) {
        tvWarningRestore.visibility = View.VISIBLE

        AlertDialog.Builder(this)
            .setTitle("Pilih Mode Restore")
            .setMessage("Bagaimana Anda ingin mengembalikan data?")
            .setPositiveButton("Replace (Ganti Semua)") { _, _ ->
                restoreFromFile(uri, RestoreMode.REPLACE)
            }
            .setNegativeButton("Merge (Gabungkan)") { _, _ ->
                restoreFromFile(uri, RestoreMode.MERGE)
            }
            .setNeutralButton("Batal", null)
            .show()
    }

    private fun restoreFromFile(uri: Uri, mode: RestoreMode) {
        try {
            // Baca file
            val jsonString = readJsonFromUri(uri)

            // Parse JSON
            val backupData = gson.fromJson(jsonString, BackupData::class.java)

            // Validasi data
            if (!validateBackupData(backupData)) {
                Toast.makeText(this, "Format backup tidak valid!", Toast.LENGTH_SHORT).show()
                return
            }

            // Konfirmasi restore
            showRestoreConfirmation(backupData, mode)

        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            Toast.makeText(this, "File backup tidak valid!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Restore gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readJsonFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun validateBackupData(backupData: BackupData): Boolean {
        return backupData.version != null &&
                backupData.backupDate != null &&
                backupData.catatan != null &&
                backupData.tugas != null
    }

    private fun showRestoreConfirmation(backupData: BackupData, mode: RestoreMode) {
        val message = when (mode) {
            RestoreMode.REPLACE -> "Semua data saat ini akan DIHAPUS dan diganti dengan data backup.\n\nData Backup:\n" +
                    "• ${backupData.catatan?.size ?: 0} Catatan\n" +
                    "• ${backupData.tugas?.size ?: 0} Tugas\n" +
                    "• ${backupData.arsipCatatanIds?.size ?: 0} Catatan Arsip\n" +
                    "• ${backupData.arsipTugasIds?.size ?: 0} Tugas Arsip\n\n" +
                    "Lanjutkan?"

            RestoreMode.MERGE -> "Data backup akan DIGABUNGKAN dengan data saat ini.\n" +
                    "Data duplikat akan dilewati.\n\n" +
                    "Data Backup:\n" +
                    "• ${backupData.catatan?.size ?: 0} Catatan\n" +
                    "• ${backupData.tugas?.size ?: 0} Tugas\n" +
                    "• ${backupData.arsipCatatanIds?.size ?: 0} Catatan Arsip\n" +
                    "• ${backupData.arsipTugasIds?.size ?: 0} Tugas Arsip\n\n" +
                    "Lanjutkan?"
        }

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Restore")
            .setMessage(message)
            .setPositiveButton("Ya, Restore") { _, _ ->
                performRestore(backupData, mode)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performRestore(backupData: BackupData, mode: RestoreMode) {
        try {
            when (mode) {
                RestoreMode.REPLACE -> {
                    // Hapus semua data lama
                    catatanPrefs.saveCatatanList(emptyList())
                    tugasPrefs.saveList(emptyList())
                    arsipPrefs.saveArsipCatatan(emptyList())
                    arsipPrefs.saveArsipTugas(emptyList())

                    // Simpan data backup
                    backupData.catatan?.let { catatanPrefs.saveCatatanList(it) }
                    backupData.tugas?.let { tugasPrefs.saveList(it) }
                    backupData.arsipCatatanIds?.let { arsipPrefs.saveArsipCatatan(it) }
                    backupData.arsipTugasIds?.let { arsipPrefs.saveArsipTugas(it) }
                }

                RestoreMode.MERGE -> {
                    // Gabungkan data
                    backupData.catatan?.let { backupCatatan ->
                        val existingCatatan = catatanPrefs.getCatatanList().toMutableList()
                        val existingIds = existingCatatan.map { it.id }.toSet()

                        // Tambahkan hanya yang belum ada
                        val newCatatan = backupCatatan.filter { it.id !in existingIds }
                        existingCatatan.addAll(newCatatan)
                        catatanPrefs.saveCatatanList(existingCatatan)
                    }

                    backupData.tugas?.let { backupTugas ->
                        val existingTugas = tugasPrefs.getAllTugas().toMutableList()
                        val existingIds = existingTugas.map { it.id }.toSet()

                        val newTugas = backupTugas.filter { it.id !in existingIds }
                        existingTugas.addAll(newTugas)
                        tugasPrefs.saveList(existingTugas)
                    }

                    // Merge arsip IDs
                    backupData.arsipCatatanIds?.let { backupIds ->
                        val existingIds = arsipPrefs.getArsipCatatan().toMutableSet()
                        existingIds.addAll(backupIds)
                        arsipPrefs.saveArsipCatatan(existingIds.toList())
                    }

                    backupData.arsipTugasIds?.let { backupIds ->
                        val existingIds = arsipPrefs.getArsipTugas().toMutableSet()
                        existingIds.addAll(backupIds)
                        arsipPrefs.saveArsipTugas(existingIds.toList())
                    }
                }
            }

            // Update user data (jika ada di backup)
            backupData.user?.let { backupUser ->
                val currentUserId = sessionManager.getUserId()
                if (currentUserId != null) {
                    // Update user info, tapi keep current userId
                    val updatedUser = backupUser.copy(idNama = currentUserId)
                    userManager.updateUser(updatedUser)
                }
            }

            Toast.makeText(this, "Restore berhasil!", Toast.LENGTH_SHORT).show()

            // Restart app atau refresh
            showRestartDialog()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Restore gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRestartDialog() {
        AlertDialog.Builder(this)
            .setTitle("Restore Berhasil")
            .setMessage("Data telah dipulihkan. Aplikasi akan di-restart untuk menerapkan perubahan.")
            .setPositiveButton("Restart") { _, _ ->
                restartApp()
            }
            .setCancelable(false)
            .show()
    }

    private fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }

    // =====================================
    // DATA CLASSES
    // =====================================

    data class BackupData(
        val version: String,
        val backupDate: String,
        val user: User?,
        val catatan: List<Catatan>?,
        val tugas: List<Tugas>?,
        val arsipCatatanIds: List<String>?,
        val arsipTugasIds: List<String>?
    )

    enum class RestoreMode {
        REPLACE, MERGE
    }
}

// =====================================
// BACKUP PREFERENCES (Helper Class)
// =====================================

class BackupPreferences(context: android.content.Context) {
    private val prefs = context.getSharedPreferences("BackupPrefs", android.content.Context.MODE_PRIVATE)

    fun saveLastBackupDate(date: String) {
        prefs.edit().putString("last_backup_date", date).apply()
    }

    fun getLastBackupDate(): String? {
        return prefs.getString("last_backup_date", null)
    }
}