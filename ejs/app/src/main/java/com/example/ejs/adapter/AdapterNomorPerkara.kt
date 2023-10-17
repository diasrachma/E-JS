package com.example.ejs.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ejs.R
import com.example.ejs.admin.BuktiDetailAdminActivity
import com.example.ejs.admin.NomorPerkaraActivity
import kotlinx.android.synthetic.main.activity_nomor_perkara.*

class AdapterNomorPerkara (val dataNp: List<HashMap<String,String>>, val parent : NomorPerkaraActivity) : RecyclerView.Adapter<AdapterNomorPerkara.HolderDataNp>() {
    class HolderDataNp(v : View) : RecyclerView.ViewHolder(v) {
        val idNp = v.findViewById<TextView>(R.id.tvIdNomorPerkara)
        val namaNp = v.findViewById<TextView>(R.id.tvNamaNp)
        val namaPegawai = v.findViewById<TextView>(R.id.tvNamaPegawaiNp)
        val sts = v.findViewById<View>(R.id.NpStatus)
//        val hapusNp = v.findViewById<ImageButton>(R.id.btnHapusNomorPerkara)
        val card = v.findViewById<CardView>(R.id.cardNomorPerkara)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataNp {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_nomor_perkara, parent, false)
        return HolderDataNp(v)
    }

    override fun onBindViewHolder(holder: HolderDataNp, position: Int) {
        val data = dataNp.get(position)
        holder.idNp.setText(data.get("id_np"))
        holder.namaNp.setText(data.get("nama_np"))
        holder.namaPegawai.setText(data.get("nama"))

        val sts = data.get("status_np").toString()
        if (sts.equals("aktif")) {
            holder.sts.visibility = View.VISIBLE
        } else {
            holder.sts.visibility = View.GONE

            holder.card.setOnClickListener {
                val intent = Intent(it.context, BuktiDetailAdminActivity::class.java)
                intent.putExtra("idBukti",data.get("id_bukti"))
                it.context.startActivity(intent)
            }
        }

        if(position.rem(2)==0) holder.card.setCardBackgroundColor(Color.parseColor("#5A5A5A"))
        else holder.card.setCardBackgroundColor(Color.parseColor("#939393"))

//        holder.hapusNp.setOnClickListener { v : View ->
//            parent.tvBayanganId.setText(data.get("id_np"))
//
//            AlertDialog.Builder(v.context)
//                .setIcon(R.drawable.warning)
//                .setTitle("Peringatan!")
//                .setMessage("Apakah Anda ingin menghapus nomor perkara "+holder.namaNp.text.toString())
//                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
//                    parent.deleteNp("delete")
//                    parent.restartActivity()
//                    Toast.makeText(v.context, "Berhasil menghapus nomor perkara "+holder.namaNp.text.toString(), Toast.LENGTH_LONG).show()
//                })
//                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
//                    Toast.makeText(v.context, "Membatalkan penghapusan nomor perkara "+holder.namaNp.text.toString(), Toast.LENGTH_LONG).show()
//                })
//                .show()
//        }
    }


    override fun getItemCount(): Int {
        return dataNp.size
    }
}