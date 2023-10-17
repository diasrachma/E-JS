package com.example.ejs.adapter

import android.content.Intent
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
import com.example.ejs.QRFragment
import com.example.ejs.R
import com.example.ejs.admin.BuktiDetailAdminActivity
import com.example.ejs.admin.UsersArsipAdminActivity
import com.squareup.picasso.Picasso

class AdapterUsersArsipAdmin (val dataBuktiPegawai: List<HashMap<String,String>>, val kepala : UsersArsipAdminActivity)
    : RecyclerView.Adapter<AdapterUsersArsipAdmin.HolderBuktiPerpegawai>() {
    class HolderBuktiPerpegawai(v : View) : RecyclerView.ViewHolder(v) {
        val jamtanggal = v.findViewById<TextView>(R.id.tvTanggalBuktiPegawai)
        val id_bukti = v.findViewById<TextView>(R.id.tvIdBuktiPegawai)
        val perkara = v.findViewById<TextView>(R.id.tvPerkaraBuktiPegawai)
        val foto = v.findViewById<ImageView>(R.id.imgBuktiPegawai)
        val card = v.findViewById<CardView>(R.id.cardBuktiPegawai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderBuktiPerpegawai {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_arsip_users, parent, false)
        return HolderBuktiPerpegawai(v)
    }

    override fun onBindViewHolder(holder: HolderBuktiPerpegawai, position: Int) {
        val data = dataBuktiPegawai.get(position)
        holder.jamtanggal.setText(data.get("jamtanggal"))
        holder.id_bukti.setText(data.get("id_bukti"))
        holder.perkara.setText(data.get("nomor_perkara"))
        Picasso.get().load(data.get("url")).into(holder.foto)

        holder.card.setOnClickListener { v : View ->
            val intent = Intent(v.context, BuktiDetailAdminActivity::class.java)
            intent.putExtra("idBukti",data.get("id_bukti"))
            intent.putExtra("nm",data.get("nama"))
            intent.putExtra("jbtn",data.get("jabatan"))
            intent.putExtra("kdHakim", kepala.terima_kd)
            v.context.startActivity(intent)
        }

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

        if(position.rem(2)==0) holder.card.setCardBackgroundColor(Color.parseColor("#F5EDDC"))
        else holder.card.setCardBackgroundColor(Color.parseColor("#CFD2CF"))
    }

    override fun getItemCount(): Int {
        return dataBuktiPegawai.size
    }
}