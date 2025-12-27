package com.example.LumiNote

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class TentangKamiActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var tvEmail: TextView

    // Social & Share Cards
    private lateinit var cardGithub: CardView
    private lateinit var cardWhatsApp: CardView
    private lateinit var cardInstagram: CardView
    private lateinit var cardShareApp: CardView

    // Links & Info
    private val githubUrl = "https://github.com/kurniawan-Donn/LumiNote_1.1.0"
    private val whatsappNumber = "+6282133237136"
    private val instagramUsername = "kurniawandony7"
    private val emailAddress = "Dkk138@gmail.com"

    // APK Info
    private val apkDownloadUrl = "https://github.com/kurniawan-Donn/LumiNote_1.1.0/releases/download/v1.1.0/LumiNote-v1.1.0.apk"
    private val appVersion = "1.1.0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentang_kami)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        tvEmail = findViewById(R.id.tvEmail)

        // Initialize social cards
        cardGithub = findViewById(R.id.cardGithub)
        cardWhatsApp = findViewById(R.id.cardWhatsApp)
        cardInstagram = findViewById(R.id.cardInstagram)
        cardShareApp = findViewById(R.id.cardShareApp)
    }

    private fun setupListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Email - auto link sudah di XML, tapi tambah copy to clipboard
        tvEmail.setOnLongClickListener {
            copyToClipboard(emailAddress, "Email")
            true
        }

        // GitHub Repository
        cardGithub.setOnClickListener {
            openUrl(githubUrl, "GitHub")
        }

        // WhatsApp Contact
        cardWhatsApp.setOnClickListener {
            openWhatsApp()
        }

        // Instagram
        cardInstagram.setOnClickListener {
            openInstagram()
        }

        // Share App
        cardShareApp.setOnClickListener {
            showShareDialog()
        }
    }

    // =====================================
    // OPEN LINKS
    // =====================================

    private fun openUrl(url: String, platform: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak dapat membuka $platform", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun openWhatsApp() {
        try {
            // Format pesan default
            val message = "P Don info Ngopi Bolo. Ghassshh 🍵🍵 "
            val encodedMessage = Uri.encode(message)

            // Coba buka WhatsApp langsung
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/$whatsappNumber?text=$encodedMessage")

            // Cek apakah WhatsApp terinstall
            if (isAppInstalled("com.whatsapp")) {
                intent.setPackage("com.whatsapp")
            }

            startActivity(intent)
        } catch (e: Exception) {
            // Fallback: Copy nomor ke clipboard
            copyToClipboard(whatsappNumber, "Nomor WhatsApp")
            Toast.makeText(this, "WhatsApp tidak ditemukan. Nomor disalin!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openInstagram() {
        try {
            // Coba buka Instagram app
            val intent = Intent(Intent.ACTION_VIEW)

            if (isAppInstalled("com.instagram.android")) {
                // Buka di app Instagram
                intent.data = Uri.parse("http://instagram.com/_u/$instagramUsername")
                intent.setPackage("com.instagram.android")
            } else {
                // Buka di browser
                intent.data = Uri.parse("https://www.instagram.com/$instagramUsername")
            }

            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak dapat membuka Instagram", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // =====================================
    // SHARE APP
    // =====================================

    private fun showShareDialog() {
        val options = arrayOf(
            "📤 Bagikan File APK",
            "🔗 Bagikan Link Download",
            "📋 Salin Link Download"
        )

        AlertDialog.Builder(this)
            .setTitle("Bagikan Aplikasi")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> shareApkFile()
                    1 -> shareDownloadLink()
                    2 -> copyDownloadLink()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun shareApkFile() {
        try {
            val packageName = packageName
            val packageManager = packageManager
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val apkPath = packageInfo.applicationInfo?.sourceDir

            if (apkPath == null) {
                Toast.makeText(this, "Error: File APK tidak ditemukan", Toast.LENGTH_SHORT).show()
                shareDownloadLink()
                return
            }

            val apkUri = androidx.core.content.FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                java.io.File(apkPath)
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, apkUri)
                putExtra(Intent.EXTRA_SUBJECT, "LumiNote - Aplikasi Catatan & Tugas")
                putExtra(Intent.EXTRA_TEXT,
                    "Coba aplikasi LumiNote! 📝✨\n\n" +
                            "Aplikasi catatan dan tugas yang membantu produktivitas harianmu.\n\n" +
                            "Versi: $appVersion\n" +
                            "GitHub: $githubUrl"
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Bagikan APK via"))
        } catch (e: PackageManager.NameNotFoundException) {
            Toast.makeText(this, "Error: File APK tidak ditemukan", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            shareDownloadLink()
        } catch (e: Exception) {
            Toast.makeText(this, "Error membagikan APK: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()

            // Fallback ke share link
            shareDownloadLink()
        }
    }

    private fun shareDownloadLink() {
        val shareText = """
            📝 LumiNote - Aplikasi Catatan & Tugas
            
            Aplikasi catatan dan tugas yang membantu produktivitas harianmu! ✨
            
            ✨ Fitur:
            • Catatan & Tugas
            • Favorit & Arsip
            • Statistik Produktivitas
            • Backup & Restore
            
            📥 Download APK:
            $apkDownloadUrl
            
            🐙 GitHub Repository:
            $githubUrl
            
            Versi: $appVersion
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "LumiNote - Aplikasi Catatan & Tugas")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Bagikan via"))
    }

    private fun copyDownloadLink() {
        copyToClipboard(apkDownloadUrl, "Link Download")

        // Tampilkan info tambahan
        AlertDialog.Builder(this)
            .setTitle("Link Download Disalin! 📋")
            .setMessage(
                "Link download APK telah disalin ke clipboard.\n\n" +
                        "Anda bisa paste link ini untuk dibagikan ke teman-teman:\n\n" +
                        apkDownloadUrl
            )
            .setPositiveButton("Oke", null)
            .show()
    }

    // =====================================
    // HELPER FUNCTIONS
    // =====================================

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "$label disalin ke clipboard! 📋", Toast.LENGTH_SHORT).show()
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}