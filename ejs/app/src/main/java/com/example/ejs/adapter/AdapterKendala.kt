package com.example.ejs.adapter

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ejs.R
import com.example.ejs.pegawai.KendalaPegawaiActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterKendala(val dataKendala: List<HashMap<String,String>>, val thisParent: KendalaPegawaiActivity)
    : RecyclerView.Adapter<AdapterKendala.HolderDataKendala>(){
    class HolderDataKendala(v : View) : RecyclerView.ViewHolder(v) {
        val pengirim = v.findViewById<TextView>(R.id.pengirimKendala)
        val textKendala = v.findViewById<TextView>(R.id.pesanKendala)
        val cardKendala = v.findViewById<CardView>(R.id.cardKendala)
        val lingkaranPegawai = v.findViewById<CircleImageView>(R.id.lingkaranPegawai)
        val lingkaranAdmin = v.findViewById<CircleImageView>(R.id.lingkaranAdmin)
        val jamKendala = v.findViewById<TextView>(R.id.jamKendala)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataKendala {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_kendala, parent, false)
        return HolderDataKendala(v)
    }

    override fun getItemCount(): Int {
        return dataKendala.size
    }

    override fun onBindViewHolder(holder: HolderDataKendala, position: Int) {
        val data = dataKendala.get(position)
        holder.pengirim.setText(data.get("nama"))
        holder.textKendala.setText(data.get("pesan"))
        holder.jamKendala.setText(data.get("jam_kendala"))

        holder.cardKendala.setOnClickListener {
            thisParent.idK = data.get("id_kendala").toString()

            val contextMenu = PopupMenu(thisParent, it)
            contextMenu.menuInflater.inflate(R.menu.menu_context, contextMenu.menu)
            contextMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.context_hapus -> {
                        AlertDialog.Builder(thisParent)
                            .setIcon(android.R.drawable.stat_sys_warning)
                            .setTitle("Batalkan!")
                            .setMessage("Apakah Anda membatalkan pesan ini?")
                            .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                                thisParent.delete("delete")
                                thisParent.restartActivity()
                            })
                            .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                            })
                            .show()
                        true
                    }
                }
                false
            }
            contextMenu.show()
        }

        val lvl = data.get("level")
        if (lvl.toString().equals("Pegawai")) {
            holder.cardKendala.setCardBackgroundColor(Color.parseColor("#FDEEBF"))
            Picasso.get().load(data.get("foto")).into(holder.lingkaranPegawai)
            holder.lingkaranAdmin.visibility = View.GONE
        } else if (lvl.toString().equals("Hakim")) {
            holder.cardKendala.setCardBackgroundColor(Color.parseColor("#a4a1e9"))
            Picasso.get().load(data.get("foto")).into(holder.lingkaranAdmin)
            holder.lingkaranPegawai.visibility = View.GONE
        } else {
            holder.cardKendala.setCardBackgroundColor(Color.parseColor("#AAFFFE"))
            Picasso.get().load(data.get("foto")).into(holder.lingkaranAdmin)
            holder.lingkaranPegawai.visibility = View.GONE
        }
    }
}
