package com.example.my_aplication  // Mendeklarasikan package/namespace untuk aplikasi

// ===============================
// IMPORTS
// ===============================
import android.view.LayoutInflater  // Untuk mengubah XML layout menjadi View object
import android.view.View  // Class dasar untuk komponen UI
import android.view.ViewGroup  // Container untuk kumpulan View
import android.widget.CheckBox  // Komponen checkbox untuk status selesai
import android.widget.ImageView  // Komponen untuk menampilkan gambar/icon
import android.widget.TextView  // Komponen untuk menampilkan teks
import androidx.recyclerview.widget.RecyclerView  // Adapter untuk RecyclerView

// ===============================
// CLASS TUGAS ADAPTER
// Adapter untuk RecyclerView yang menampilkan daftar tugas
// ===============================
class TugasAdapter(
    private val listTugas: MutableList<Tugas>,  // Daftar tugas yang akan ditampilkan (mutable/dapat diubah)
    private val onEditClick: (Tugas) -> Unit,  // Lambda function untuk handle klik edit
    private val onDeleteClick: (Tugas) -> Unit,  // Lambda function untuk handle klik hapus
    private val onCheckedChange: (Tugas, Boolean) -> Unit  // Lambda function untuk handle perubahan checkbox
) : RecyclerView.Adapter<TugasAdapter.TugasViewHolder>() {  // Extends RecyclerView.Adapter dengan ViewHolder kustom

    // ===============================
    // INNER CLASS TUGAS VIEW HOLDER
    // Menyimpan referensi ke view components dalam setiap item list
    // ===============================
    class TugasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Mendeklarasikan semua view yang ada dalam layout item_tugas.xml
        val checkbox: CheckBox = itemView.findViewById(R.id.centang)  // Checkbox untuk status selesai
        val judul: TextView = itemView.findViewById(R.id.tugas_judul)  // TextView untuk judul tugas
        val deskripsi: TextView = itemView.findViewById(R.id.tugas_deskripsi)  // TextView untuk deskripsi tugas
        val tanggal: TextView = itemView.findViewById(R.id.tgltugas)  // TextView untuk tanggal tugas
        val btnEdit: ImageView = itemView.findViewById(R.id.ic_edit_tugas)  // ImageView untuk tombol edit
        val btnHapus: ImageView = itemView.findViewById(R.id.ic_hapus_tugas)  // ImageView untuk tombol hapus
    }

    // ===============================
    // METHOD onCreateViewHolder
    // Dipanggil saat RecyclerView membutuhkan ViewHolder baru
    // ===============================
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasViewHolder {
        // Meng-inflate layout item_tugas.xml menjadi View object
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tugas, parent, false)
        return TugasViewHolder(view)  // Mengembalikan ViewHolder baru dengan view yang sudah di-inflate
    }

    // ===============================
    // METHOD onBindViewHolder
    // Dipanggil untuk mengisi data ke ViewHolder pada posisi tertentu
    // ===============================
    override fun onBindViewHolder(holder: TugasViewHolder, position: Int) {
        val tugas = listTugas[position]  // Mendapatkan objek Tugas pada posisi tertentu

        // ===============================
        // SET DATA KE VIEW COMPONENTS
        // ===============================
        holder.judul.text = tugas.judul  // Meng-set teks judul dari objek Tugas
        holder.deskripsi.text = tugas.deskripsi  // Meng-set teks deskripsi
        holder.tanggal.text = tugas.tanggal ?: ""  // Meng-set teks tanggal, jika null gunakan string kosong

        // ===============================
        // UBAH WARNA BERDASARKAN STATUS SELESAI
        // ===============================
        if (tugas.isSelesai) {
            // Jika tugas sudah selesai, gunakan warna abu-abu gelap
            holder.judul.setTextColor(holder.judul.context.getColor(android.R.color.darker_gray))
            holder.deskripsi.setTextColor(holder.deskripsi.context.getColor(android.R.color.darker_gray))
            holder.tanggal.setTextColor(holder.tanggal.context.getColor(android.R.color.darker_gray))
        } else {
            // Jika tugas belum selesai, gunakan warna hitam (default)
            holder.judul.setTextColor(holder.judul.context.getColor(android.R.color.black))
            holder.deskripsi.setTextColor(holder.deskripsi.context.getColor(android.R.color.black))
            holder.tanggal.setTextColor(holder.tanggal.context.getColor(android.R.color.black))
        }

        // ===============================
        // HANDLE CHECKBOX STATUS SELESAI
        // ===============================
        holder.checkbox.setOnCheckedChangeListener(null)  // Remove listener sementara untuk menghindari loop
        holder.checkbox.isChecked = tugas.isChecked()  // Set status checkbox berdasarkan data
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            // Ketika checkbox diubah, panggil callback dengan objek Tugas dan status baru
            onCheckedChange(tugas, isChecked)
        }

        // ===============================
        // HANDLE KLIK TOMBOL EDIT
        // ===============================
        holder.btnEdit.setOnClickListener {
            // Ketika tombol edit diklik, panggil callback dengan objek Tugas yang sesuai
            onEditClick(tugas)
        }

        // ===============================
        // HANDLE KLIK TOMBOL HAPUS
        // ===============================
        holder.btnHapus.setOnClickListener {
            // Ketika tombol hapus diklik, panggil callback dengan objek Tugas yang sesuai
            onDeleteClick(tugas)
        }
    }

    // ===============================
    // METHOD getItemCount
    // Mengembalikan jumlah total item dalam daftar
    // ===============================
    override fun getItemCount(): Int = listTugas.size  // Mengembalikan ukuran listTugas

    // ===============================
    // METHOD updateData
    // Helper method untuk memperbarui data adapter dengan daftar baru
    // ===============================
    fun updateData(newList: List<Tugas>) {
        listTugas.clear()  // Menghapus semua data lama dari list
        listTugas.addAll(newList)  // Menambahkan semua data baru ke list
        notifyDataSetChanged()  // Memberi tahu adapter bahwa data telah berubah (refresh UI)
    }
}