package com.example.ejs.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ejs.QRCodeFragment
import com.example.ejs.QRFragment
import com.example.ejs.R
import com.example.ejs.pegawai.BuktiRiwayatPegawaiActivity
import com.example.ejs.pegawai.MainPegawaiActivity
import com.squareup.picasso.Picasso

class AdapterBuktiMainPegawai (val dataBuktiPegawai: List<HashMap<String,String>>, val kepala : MainPegawaiActivity)
    : RecyclerView.Adapter<AdapterBuktiMainPegawai.HolderBuktiPegawai>() {
    class HolderBuktiPegawai(v : View) : RecyclerView.ViewHolder(v) {
        val jamtanggal = v.findViewById<TextView>(R.id.tvTanggalBuktiPegawai)
        val id_bukti = v.findViewById<TextView>(R.id.tvIdBuktiPegawai)
        val perkara = v.findViewById<TextView>(R.id.tvPerkaraBuktiPegawai)
        val foto = v.findViewById<ImageView>(R.id.imgBuktiPegawai)
        val card = v.findViewById<CardView>(R.id.cardBuktiPegawai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBuktiPegawai {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_bukti_pegawai, parent, false)
        return HolderBuktiPegawai(v)
    }

    override fun onBindViewHolder(holder: HolderBuktiPegawai, position: Int) {
        val data = dataBuktiPegawai.get(position)
        holder.jamtanggal.setText(data.get("jamtanggal"))
        holder.id_bukti.setText(data.get("id_bukti"))
        holder.perkara.setText(data.get("nomor_perkara"))
        Picasso.get().load(data.get("url")).into(holder.foto)

        holder.card.setOnLongClickListener {
            var contextMenu = PopupMenu(it.context, it)
            contextMenu.menuInflater.inflate(R.menu.menu_qr, contextMenu.menu)
            contextMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.contextQr -> {
                        var dialog = QRFragment()

                        val bundle = Bundle()
                        bundle.putString("kode", data.get("id_bukti"))
                        dialog.arguments = bundle
                        dialog.show(kepala.supportFragmentManager, "qrFragment")
                    }
                }
                false
            }
            contextMenu.show()
            true
        }

        if(position.rem(2)==0) holder.card.setCardBackgroundColor(Color.parseColor("#d1ffcd"))
        else holder.card.setCardBackgroundColor(Color.parseColor("#00f7dc"))
    }

    override fun getItemCount(): Int {
        return dataBuktiPegawai.size
    }
}