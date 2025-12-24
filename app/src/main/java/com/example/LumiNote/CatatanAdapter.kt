// Deklarasi package untuk organisasi kode
package com.example.LumiNote

// Mengimpor LayoutInflater untuk mengubah layout XML menjadi View
import android.view.LayoutInflater
// Mengimpor View sebagai komponen dasar UI
import android.view.View
// Mengimpor ViewGroup sebagai container untuk view
import android.view.ViewGroup
// Mengimpor ImageView untuk menampilkan gambar/ikon
import android.widget.ImageView
// Mengimpor TextView untuk menampilkan teks
import android.widget.TextView
// Mengimpor ConstraintLayout untuk layout yang fleksibel
import androidx.constraintlayout.widget.ConstraintLayout
// Mengimpor RecyclerView untuk menampilkan daftar data yang efisien
import androidx.recyclerview.widget.RecyclerView
// Mengimpor Calendar untuk manipulasi tanggal dan waktu
import java.util.Calendar

// Deklarasi kelas CatatanAdapter yang mewarisi RecyclerView.Adapter
// Adapter ini bertanggung jawab untuk menghubungkan data catatan dengan tampilan di RecyclerView
class CatatanAdapter(
    private var catatanList: List<Catatan>,           // Daftar data catatan (bisa diubah dengan updateData)
    private val onItemClick: (Catatan) -> Unit,      // Lambda callback saat item catatan diklik
    private val onEditClick: (Catatan) -> Unit,      // Lambda callback saat tombol edit diklik
    private val onDeleteClick: (Catatan) -> Unit     // Lambda callback saat tombol hapus diklik
) : RecyclerView.Adapter<CatatanAdapter.CatatanViewHolder>() {  // Menggunakan ViewHolder pattern

    // ===============================
    // ViewHolder Class
    // ===============================
    // ViewHolder menyimpan referensi ke view dalam setiap item daftar
    class CatatanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Layout utama (card) untuk setiap item catatan
        val kartuCatatan: ConstraintLayout = view.findViewById(R.id.kartucatatan)
        // TextView untuk menampilkan judul catatan
        val judulTextView: TextView = view.findViewById(R.id.catatan_judul)
        // TextView untuk menampilkan deskripsi catatan
        val deskripsiTextView: TextView = view.findViewById(R.id.catatan_deskripsi)
        // TextView untuk menampilkan tanggal dan waktu catatan
        val tanggalTextView: TextView = view.findViewById(R.id.tglcatatan)
        // ImageView untuk tombol edit
        val editIcon: ImageView = view.findViewById(R.id.ic_edit_catatan)
        // ImageView untuk tombol hapus
        val deleteIcon: ImageView = view.findViewById(R.id.ic_hapus_catatan)
    }

    // ===============================
    // onCreateViewHolder
    // ===============================
    // Dipanggil ketika RecyclerView membutuhkan ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatatanViewHolder {
        // Menginflate layout item_catatan.xml menjadi View
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catatan, parent, false)
        // Mengembalikan instance CatatanViewHolder dengan view yang sudah diinflate
        return CatatanViewHolder(view)
    }

    // ===============================
    // onBindViewHolder
    // ===============================
    // Dipanggil untuk mengikat data ke ViewHolder pada posisi tertentu
    override fun onBindViewHolder(holder: CatatanViewHolder, position: Int) {
        // Mendapatkan objek Catatan pada posisi tertentu dari daftar
        val catatan = catatanList[position]

        // Mendapatkan kalender saat ini untuk tanggal dan waktu default
        val currentCalendar = Calendar.getInstance()

        // Format tanggal default: "DD/MM/YYYY"
        val defaultTanggal = String.format("%02d/%02d/%04d",
            currentCalendar.get(Calendar.DAY_OF_MONTH),      // Hari (2 digit)
            currentCalendar.get(Calendar.MONTH) + 1,         // Bulan (ditambah 1 karena bulan dimulai dari 0)
            currentCalendar.get(Calendar.YEAR)               // Tahun (4 digit)
        )

        // Format waktu default: "HH:MM" (24 jam)
        val defaultWaktu = String.format("%02d:%02d",
            currentCalendar.get(Calendar.HOUR_OF_DAY),       // Jam (0-23)
            currentCalendar.get(Calendar.MINUTE)             // Menit
        )

        // Mengatur teks judul dari objek catatan
        holder.judulTextView.text = catatan.judul

        // Mengatur teks deskripsi dengan format bullet point
        holder.deskripsiTextView.text = if (catatan.deskripsi.isNotEmpty()) {
            // Jika deskripsi tidak kosong, tambahkan bullet point di depan
            "• ${catatan.deskripsi}"
        } else {
            // Jika deskripsi kosong, tampilkan pesan default dengan bullet point
            "• Tidak ada deskripsi"
        }

        // Mengatur teks tanggal dan waktu
        // Gunakan tanggal dari catatan jika ada, jika tidak gunakan tanggal default
        val tanggal = if (!catatan.tanggal.isNullOrEmpty()) catatan.tanggal else defaultTanggal
        // Gunakan waktu dari catatan jika ada, jika tidak gunakan waktu default
        val waktu = if (!catatan.waktu.isNullOrEmpty()) catatan.waktu else defaultWaktu
        // Gabungkan tanggal dan waktu dengan spasi
        holder.tanggalTextView.text = "$tanggal $waktu"

        // ===============================
        // Menangani klik pada item (seluruh kartu)
        // ===============================
        holder.kartuCatatan.setOnClickListener {
            // Memanggil callback onItemClick dengan objek catatan yang diklik
            onItemClick(catatan)
        }

        // ===============================
        // Menangani klik pada ikon edit
        // ===============================
        holder.editIcon.setOnClickListener {
            // Memanggil callback onEditClick dengan objek catatan yang akan diedit
            onEditClick(catatan)
        }

        // ===============================
        // Menangani klik pada ikon hapus
        // ===============================
        holder.deleteIcon.setOnClickListener {
            // Memanggil callback onDeleteClick dengan objek catatan yang akan dihapus
            onDeleteClick(catatan)
        }
    }

    // ===============================
    // getItemCount
    // ===============================
    // Mengembalikan jumlah item dalam daftar catatan
    override fun getItemCount(): Int = catatanList.size

    // ===============================
    // Update data adapter
    // ===============================
    // Fungsi untuk memperbarui data di adapter dengan daftar baru
    fun updateData(newList: List<Catatan>) {
        // Mengganti daftar catatan lama dengan yang baru
        catatanList = newList
        // Memberitahu RecyclerView bahwa semua data mungkin telah berubah
        // Catatan: notifyDataSetChanged() kurang efisien untuk perubahan kecil
        // Alternatif: DiffUtil untuk perubahan yang lebih efisien
        notifyDataSetChanged()
    }
}