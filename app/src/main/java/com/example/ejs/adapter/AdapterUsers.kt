package com.example.ejs.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ejs.R
import com.example.ejs.admin.UsersAdminActivity
import com.example.ejs.admin.UsersArsipAdminActivity
import com.example.ejs.admin.UsersEditAdminActivity
import com.example.ejs.hakim.ProfilPegawaiActivity
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.row_users.*

class AdapterUsers(val dataUsers : List<HashMap<String,String>>, val kepala : UsersAdminActivity) : RecyclerView.Adapter<AdapterUsers.HolderDataUsers>() {
    class HolderDataUsers(v : View) : RecyclerView.ViewHolder(v) {
        val namaUsers = v.findViewById<TextView>(R.id.namaPegawai)
        val jabatanUsers = v.findViewById<TextView>(R.id.jabatanPegawai)
        val nipUsers = v.findViewById<TextView>(R.id.nipPegawai)
        val golonganUsers = v.findViewById<TextView>(R.id.golonganPegawai)
        val unitkerjaUsers = v.findViewById<TextView>(R.id.unitkerjaPegawai)
        val masakerjaUsers = v.findViewById<TextView>(R.id.masakerjaPegawai)
        val fotoPegawai = v.findViewById<ImageView>(R.id.imgPegawa)
        val statusUser = v.findViewById<View>(R.id.stsAkunUsers)
        val hapusPegawai = v.findViewById<Button>(R.id.btnHapusPegawai)
        val aktifkanPegawai = v.findViewById<Button>(R.id.btnAktifkanPegawai)
        val editPegawai = v.findViewById<Button>(R.id.btnEditPegawai)
        val arsip = v.findViewById<MaterialCardView>(R.id.btnLihatArsip)
        val cardUsers = v.findViewById<CardView>(R.id.cardPegawai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataUsers {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_users, parent, false)
        return HolderDataUsers(v)
    }

    override fun onBindViewHolder(holder: HolderDataUsers, position: Int) {
        val data = dataUsers.get(position)
        holder.namaUsers.setText(data.get("nama"))
        holder.jabatanUsers.setText(data.get("jabatan"))
        holder.nipUsers.setText(data.get("nip"))
        holder.golonganUsers.setText(data.get("golongan"))
        holder.unitkerjaUsers.setText(data.get("unit_kerja"))
        holder.masakerjaUsers.setText(data.get("masa_kerja"))
        Picasso.get().load(data.get("foto")).into(holder.fotoPegawai)

        val sts = data.get("sts_akun").toString()
        if (sts.equals("AKTIF")) {
            holder.aktifkanPegawai.visibility = View.GONE
            holder.hapusPegawai.visibility = View.VISIBLE
            holder.statusUser.visibility = View.VISIBLE
        } else {
            holder.aktifkanPegawai.visibility = View.VISIBLE
            holder.hapusPegawai.visibility = View.GONE
            holder.statusUser.visibility = View.GONE
        }

        if (kepala.terima_kd.equals("hakim")) {
            holder.aktifkanPegawai.visibility = View.GONE
            holder.hapusPegawai.visibility = View.GONE
            holder.editPegawai.visibility = View.GONE
        }

        val jbt = data.get("level").toString()
        if (jbt.equals("Pegawai")) {
            holder.arsip.visibility = View.VISIBLE
        } else {
            holder.arsip.visibility = View.GONE
        }

        holder.arsip.setOnClickListener { v: View ->
            val intent = Intent(v.context, UsersArsipAdminActivity::class.java)
            intent.putExtra("nip", data.get("nip").toString())
            v.context.startActivity(intent)
        }

        holder.aktifkanPegawai.setOnClickListener { v : View ->
            kepala.tvNipHapus.setText(data.get("nip"))

            AlertDialog.Builder(v.context)
                .setIcon(R.drawable.warning)
                .setTitle("Peringatan!")
                .setMessage("Apakah Anda ingin mengaktifkan akun "+holder.namaUsers.text.toString())
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    kepala.aktifkanPegawai("aktif")
                    val intent = Intent(v.context, UsersAdminActivity::class.java)
                    v.context.startActivity(intent)
                    Toast.makeText(v.context, "Berhasil mengaktifkan akun "+holder.namaUsers.text.toString(), Toast.LENGTH_LONG).show()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(v.context, "Membatalkan pengaktifan akun "+holder.namaUsers.text.toString(), Toast.LENGTH_LONG).show()
                })
                .show()
        }

        holder.hapusPegawai.setOnClickListener { v : View ->
            kepala.tvNipHapus.setText(data.get("nip"))

            AlertDialog.Builder(v.context)
                .setIcon(R.drawable.warning)
                .setTitle("Peringatan!")
                .setMessage("Apakah Anda ingin menonaktifkan akun "+holder.namaUsers.text.toString())
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    kepala.deletePegawai("non_aktif")
                    val intent = Intent(v.context, UsersAdminActivity::class.java)
                    v.context.startActivity(intent)
                    Toast.makeText(v.context, "Berhasil menonaktifkan akun "+holder.namaUsers.text.toString(), Toast.LENGTH_LONG).show()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(v.context, "Membatalkan penonaktifan akun "+holder.namaUsers.text.toString(), Toast.LENGTH_LONG).show()
                })
                .show()
        }

        holder.editPegawai.setOnClickListener { v : View ->
            val intentEditProfil = Intent(v.context, UsersEditAdminActivity::class.java)
            intentEditProfil.putExtra("nip_pegawai", data.get("nip"))
            v.context.startActivity(intentEditProfil)
        }

        val jabatan = data?.get("jabatan").toString()

        holder.cardUsers.setOnClickListener { v : View ->
            val intentBuktiPerpegawai = Intent(v.context, ProfilPegawaiActivity::class.java)
            intentBuktiPerpegawai.putExtra("nip", data.get("nip").toString())
            v.context.startActivity(intentBuktiPerpegawai)
        }
    }

    override fun getItemCount(): Int {
        return dataUsers.size
    }
}
