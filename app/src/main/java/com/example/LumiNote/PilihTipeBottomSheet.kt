// Deklarasi package untuk organisasi kode
package com.example.LumiNote

// Mengimpor Intent untuk navigasi antar activity
import android.content.Intent
// Mengimpor Bundle untuk menyimpan state fragment
import android.os.Bundle
// Mengimpor LayoutInflater untuk mengubah layout XML menjadi View
import android.view.LayoutInflater
// Mengimpor View sebagai komponen dasar UI
import android.view.View
// Mengimpor ViewGroup sebagai container untuk view
import android.view.ViewGroup
// Mengimpor Button untuk tombol interaktif
import android.widget.Button
// Mengimpor BottomSheetDialogFragment untuk dialog yang muncul dari bawah layar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// Deklarasi kelas PilihTipeBottomSheet yang mewarisi BottomSheetDialogFragment
class PilihTipeBottomSheet : BottomSheetDialogFragment() {

    // Fungsi yang dipanggil saat fragment membuat tampilan UI-nya
    override fun onCreateView(
        inflater: LayoutInflater,       // Objek untuk mengonversi XML ke View
        container: ViewGroup?,          // Container parent untuk fragment (bisa null)
        savedInstanceState: Bundle?     // State yang disimpan sebelumnya (bisa null)
    ): View? {
        // Menginflate (mengubah) layout XML dialog_tambah.xml menjadi objek View
        // Parameter false: tidak langsung menambahkan view ke container
        return inflater.inflate(R.layout.dialog_tambah, container, false)
    }

    // Fungsi yang dipanggil setelah onCreateView, saat view sudah dibuat
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Memanggil implementasi dari kelas induk (BottomSheetDialogFragment)
        super.onViewCreated(view, savedInstanceState)

        // Menghubungkan tombol Catatan dari layout dengan ID btn_catatan
        val btnCatatan: Button = view.findViewById(R.id.btn_catatan)
        // Menghubungkan tombol Tugas dari layout dengan ID btn_tugas
        val btnTugas: Button = view.findViewById(R.id.btn_tugas)

        // Menambahkan click listener pada tombol Catatan
        btnCatatan.setOnClickListener {
            // Menutup BottomSheetDialog saat tombol diklik
            dismiss()

            // Membuat Intent untuk membuka TambahCatatanActivity
            val intent = Intent(requireContext(), TambahCatatanActivity::class.java)
            // Memulai activity baru
            startActivity(intent)
        }

        // Menambahkan click listener pada tombol Tugas
        btnTugas.setOnClickListener {
            // Menutup BottomSheetDialog saat tombol diklik
            dismiss()

            // Membuat Intent untuk membuka TambahTugasActivity
            val intent = Intent(requireContext(), TambahTugasActivity::class.java)
            // Memulai activity baru
            startActivity(intent)
        }
    }
}