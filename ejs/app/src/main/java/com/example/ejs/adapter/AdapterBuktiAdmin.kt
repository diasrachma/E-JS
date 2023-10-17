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
import com.example.ejs.QRCodeFragment
import com.example.ejs.QRFragment
import com.example.ejs.R
import com.example.ejs.admin.ArsipAdminActivity
import com.example.ejs.admin.BuktiDetailAdminActivity
import com.squareup.picasso.Picasso

class AdapterBuktiAdmin(val dataBukti: List<HashMap<String,String>>, val kepala : ArsipAdminActivity)
    : RecyclerView.Adapter<AdapterBuktiAdmin.HolderDataBukti>() {
    class HolderDataBukti(v : View) : RecyclerView.ViewHolder(v) {
        val jamtanggal = v.findViewById<TextView>(R.id.tvTanggalAdmin)
        val id_bukti = v.findViewById<TextView>(R.id.tvIdBukti)
        val nama = v.findViewById<TextView>(R.id.tvNamaAdmin)
        val jabatan = v.findViewById<TextView>(R.id.tvJabatanAdmin)
        val perkara = v.findViewById<TextView>(R.id.tvPerkaraAdmin)
        val foto = v.findViewById<ImageView>(R.id.imgBuktiAdmin)
        val card = v.findViewById<CardView>(R.id.cardPegawai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataBukti {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_bukti, parent, false)
        return HolderDataBukti(v)
    }

    override fun onBindViewHolder(holder: HolderDataBukti, position: Int) {
        val data = dataBukti.get(position)
        holder.jamtanggal.setText(data.get("jamtanggal"))
        holder.id_bukti.setText(data.get("id_bukti"))
        holder.nama.setText(data.get("nama"))
        holder.perkara.setText(data.get("nomor_perkara"))
        holder.jabatan.setText(data.get("jabatan"))
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
        return dataBukti.size
    }

}