package com.example.my_aplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class CatatanAdapter(
    private var catatanList: List<Catatan>,
    private val onItemClick: (Catatan) -> Unit,
    private val onEditClick: (Catatan) -> Unit,
    private val onDeleteClick: (Catatan) -> Unit
) : RecyclerView.Adapter<CatatanAdapter.CatatanViewHolder>() {

    class CatatanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val kartuCatatan: ConstraintLayout = view.findViewById(R.id.kartucatatan)
        val judulTextView: TextView = view.findViewById(R.id.catatan_judul)
        val deskripsiTextView: TextView = view.findViewById(R.id.catatan_deskripsi)
        val tanggalTextView: TextView = view.findViewById(R.id.tglcatatan)
        val editIcon: ImageView = view.findViewById(R.id.ic_edit_catatan)
        val deleteIcon: ImageView = view.findViewById(R.id.ic_hapus_catatan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatatanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catatan, parent, false)
        return CatatanViewHolder(view)
    }

    override fun onBindViewHolder(holder: CatatanViewHolder, position: Int) {
        val catatan = catatanList[position]
        val currentCalendar = Calendar.getInstance()
        val defaultTanggal = String.format("%02d/%02d/%04d",
            currentCalendar.get(Calendar.DAY_OF_MONTH),
            currentCalendar.get(Calendar.MONTH) + 1,
            currentCalendar.get(Calendar.YEAR)
        )
        val defaultWaktu = String.format("%02d:%02d",
            currentCalendar.get(Calendar.HOUR_OF_DAY),
            currentCalendar.get(Calendar.MINUTE)
        )

        holder.judulTextView.text = catatan.judul

        // Tampilkan deskripsi dengan bullet point
        holder.deskripsiTextView.text = if (catatan.deskripsi.isNotEmpty()) {
            "• ${catatan.deskripsi}"
        } else {
            "• Tidak ada deskripsi"
        }

        // Tampilkan tanggal
        val tanggal = if (!catatan.tanggal.isNullOrEmpty()) catatan.tanggal else defaultTanggal
        val waktu = if (!catatan.waktu.isNullOrEmpty()) catatan.waktu else defaultWaktu
        holder.tanggalTextView.text = "$tanggal $waktu"

        // Handle item click (klik seluruh card)
        holder.kartuCatatan.setOnClickListener {
            onItemClick(catatan)
        }

        // Handle edit click
        holder.editIcon.setOnClickListener {
            onEditClick(catatan)
        }

        // Handle delete click
        holder.deleteIcon.setOnClickListener {
            onDeleteClick(catatan)
        }
    }

    override fun getItemCount(): Int = catatanList.size

    // Update data adapter
    fun updateData(newList: List<Catatan>) {
        catatanList = newList
        notifyDataSetChanged()
    }
}