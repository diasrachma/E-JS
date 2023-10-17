package com.example.ejs.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ejs.R

class AdapterTrash(val dataTrash: List<HashMap<String,String>>) : RecyclerView.Adapter<AdapterTrash.HolderDataTrash>() {
    class HolderDataTrash(v : View) : RecyclerView.ViewHolder(v) {
        val idTrash = v.findViewById<TextView>(R.id.tvIdCutiTrash)
        val tglBukti = v.findViewById<TextView>(R.id.tvTanggalBuktiTrash)
        val tglHapus = v.findViewById<TextView>(R.id.tvTanggalHapusTrash)
        val namaPegawai = v.findViewById<TextView>(R.id.tvNamaTrash)
        val card = v.findViewById<CardView>(R.id.cardTrash)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataTrash {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_trash, parent, false)
        return HolderDataTrash(v)
    }

    override fun onBindViewHolder(holder: HolderDataTrash, position: Int) {
        val data = dataTrash.get(position)
        holder.idTrash.setText(data.get("id_bukti"))
        holder.tglBukti.setText(data.get("tgl_bukti"))
        holder.tglHapus.setText(data.get("tgl_hapus"))
        holder.namaPegawai.setText(data.get("nama"))
        holder.card.setCardBackgroundColor(Color.parseColor("#EDE4E4"))

        if(position.rem(2)==0) holder.card.setCardBackgroundColor(Color.parseColor("#F5EDDC"))
        else holder.card.setCardBackgroundColor(Color.parseColor("#CFD2CF"))
    }


    override fun getItemCount(): Int {
        return dataTrash.size
    }
}