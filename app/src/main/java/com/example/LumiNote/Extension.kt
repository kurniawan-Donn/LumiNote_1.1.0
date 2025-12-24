// Deklarasi package untuk organisasi kode
package com.example.LumiNote

// Mengimpor kelas Activity sebagai target ekstensi fungsi
import android.app.Activity
// Mengimpor kelas Build untuk mengecek versi SDK perangkat
import android.os.Build
// Mengimpor kelas View untuk konstanta system UI visibility
import android.view.View
// Mengimpor WindowInsets untuk API level 30+ (Android R/11+)
import android.view.WindowInsets
// Mengimpor WindowInsetsController untuk API level 30+ (Android R/11+)
import android.view.WindowInsetsController

// Deklarasi fungsi ekstensi untuk kelas Activity
// Fungsi ini menyembunyikan system UI (status bar dan navigation bar) untuk pengalaman fullscreen
fun Activity.hideSystemUI() {
    // ===============================
    // Untuk Android R (API 30) ke atas (Android 11+)
    // ===============================
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // setDecorFitsSystemWindows(false): Konten akan dirender di belakang system bars
        window.setDecorFitsSystemWindows(false)

        // Mengakses WindowInsetsController untuk mengontrol system bars
        window.insetsController?.let { controller ->
            // Menyembunyikan status bars dan navigation bars
            controller.hide(
                // Menggunakan operator 'or' untuk menggabungkan tipe bars yang akan disembunyikan:
                WindowInsets.Type.statusBars() or      // Status bar (waktu, baterai, notifikasi)
                        WindowInsets.Type.navigationBars()     // Navigation bar (tombol back, home, recent apps)
            )
            // Mengatur perilaku system bars ketika pengguna menggesek
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE: System bars akan muncul sementara saat
            // pengguna menggesek dari tepi layar, lalu menghilang lagi
        }
    }
    // ===============================
    // Untuk Android di bawah R (API < 30) - metode lama
    // ===============================
    else {
        // Supress deprecation warning karena metode ini sudah deprecated di API 30+
        @Suppress("DEPRECATION")
        // Mengatur system UI visibility menggunakan flag-flag (cara lama)
        window.decorView.systemUiVisibility =
                // Menggunakan operator 'or' untuk menggabungkan beberapa flag:
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or        // Mode immersive dengan sticky behavior
                    View.SYSTEM_UI_FLAG_FULLSCREEN or      // Menyembunyikan status bar
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or // Menyembunyikan navigation bar
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or // Layout mengisi area di belakang status bar
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or // Layout mengisi area di belakang navigation bar
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE      // Menjaga layout tetap stabil saat system bars muncul/hilang

        // Penjelasan flag:
        // - IMMERSIVE_STICKY: System bars disembunyikan, muncul sementara saat digesek, lalu hilang lagi
        // - FULLSCREEN: Menyembunyikan status bar
        // - HIDE_NAVIGATION: Menyembunyikan navigation bar
        // - LAYOUT_FULLSCREEN: Konten dapat dirender di area status bar
        // - LAYOUT_HIDE_NAVIGATION: Konten dapat dirender di area navigation bar
        // - LAYOUT_STABLE: Menjaga ukuran layout tetap sama saat system bars muncul/hilang
    }
}