package com.example.my_aplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TugasAdapter(
    private val listTugas: MutableList<Tugas>,
    private val onEditClick: (Tugas) -> Unit,
    private val onDeleteClick: (Tugas) -> Unit,
    private val onCheckedChange: (Tugas, Boolean) -> Unit
) : RecyclerView.Adapter<TugasAdapter.TugasViewHolder>() {
    // ViewHolder
    class TugasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.centang)
        val judul: TextView = itemView.findViewById(R.id.tugas_judul)
        val deskripsi: TextView = itemView.findViewById(R.id.tugas_deskripsi)
        val tanggal: TextView = itemView.findViewById(R.id.tgltugas)
        val btnEdit: ImageView = itemView.findViewById(R.id.ic_edit_tugas)
        val btnHapus: ImageView = itemView.findViewById(R.id.ic_hapus_tugas)
    }
    // Inflate layout item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tugas, parent, false)
        return TugasViewHolder(view)
    }
    override fun onBindViewHolder(holder: TugasViewHolder, position: Int) {
        val tugas = listTugas[position]

        // Set data
        holder.judul.text = tugas.judul
        holder.deskripsi.text = tugas.deskripsi
        holder.tanggal.text = tugas.tanggal ?: ""
        // Ubah warna jika tugas sudah selesai
        if (tugas.isSelesai) {
            holder.judul.setTextColor(holder.judul.context.getColor(android.R.color.darker_gray))
            holder.deskripsi.setTextColor(holder.deskripsi.context.getColor(android.R.color.darker_gray))
            holder.tanggal.setTextColor(holder.tanggal.context.getColor(android.R.color.darker_gray))
        } else {
            // Warna normal
            holder.judul.setTextColor(holder.judul.context.getColor(android.R.color.black))
            holder.deskripsi.setTextColor(holder.deskripsi.context.getColor(android.R.color.black))
            holder.tanggal.setTextColor(holder.tanggal.context.getColor(android.R.color.black))
        }
        // Checkbox selesai
        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = tugas.isChecked()
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(tugas, isChecked)
        }
        // Klik edit
        holder.btnEdit.setOnClickListener {
            onEditClick(tugas)
        }
        // Klik hapus
        holder.btnHapus.setOnClickListener {
            onDeleteClick(tugas)
        }
    }
    override fun getItemCount(): Int = listTugas.size
    // Helper update list (search / refresh)
    fun updateData(newList: List<Tugas>) {
        listTugas.clear()
        listTugas.addAll(newList)
        notifyDataSetChanged()
    }
}