package com.example.ejs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ejs.R

class AdapterKomentar(val list: List<HashMap<String,String>>) :
    RecyclerView.Adapter<AdapterKomentar.HolderDataAdapter>() {
    class HolderDataAdapter (v : View) : RecyclerView.ViewHolder(v) {
        val nm = v.findViewById<TextView>(R.id.komen_nama)
        val kom = v.findViewById<TextView>(R.id.komen_deskripsi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataAdapter {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_komentar, parent, false)
        return HolderDataAdapter(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HolderDataAdapter, position: Int) {
        val data = list.get(position)
        holder.nm.setText(data.get("nama_komentar"))
        holder.kom.setText(data.get("teks_komentar"))
    }
}