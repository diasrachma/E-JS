package com.example.ejs.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ejs.R
import com.example.ejs.hakim.FragmentLaporan
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AdapterLaporan(val list: List<HashMap<String,String>>) :
    RecyclerView.Adapter<AdapterLaporan.HolderDataAdapter>() {
    class HolderDataAdapter (v : View) : RecyclerView.ViewHolder(v) {
        val nm = v.findViewById<TextView>(R.id.lapNama)
        val img = v.findViewById<CircleImageView>(R.id.lapImg)
        val tkr = v.findViewById<TextView>(R.id.lapTerkirim)
        val tlt = v.findViewById<TextView>(R.id.lapTerlambat)
        val cd = v.findViewById<CardView>(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataAdapter {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_laporan, parent, false)
        return HolderDataAdapter(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HolderDataAdapter, position: Int) {
        val data = list.get(position)
        holder.nm.setText(data.get("nama"))
        Picasso.get().load(data.get("img")).into(holder.img)

        val terkirim = data.get("terkirim").toString()
        val terlambat = data.get("terlambat").toString()
        val terkirimText = "Kirim Evidence : $terkirim kali"
        val terlambatText = "Nomor Perkara Aktif : $terlambat"

        val spannableTerkirimString = SpannableString(terkirimText)
        val spannableTerlambatString = SpannableString(terlambatText)

        val terkirimColor = Color.BLUE
        val terlambatColor = Color.parseColor("#FF39CD3F")

        val terkirimStartIndex = terkirimText.indexOf(terkirim)
        val terlambatStartIndex = terlambatText.indexOf(terlambat)

        val terkirimEndIndex = terkirimStartIndex + terkirim.length
        val terlambatEndIndex = terlambatStartIndex + terlambat.length

        // Menambahkan efek tebal (bold) pada teks terkirim dan terlambat
        spannableTerkirimString.setSpan(ForegroundColorSpan(terkirimColor), terkirimStartIndex, terkirimEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableTerkirimString.setSpan(StyleSpan(Typeface.BOLD), terkirimStartIndex, terkirimEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableTerlambatString.setSpan(ForegroundColorSpan(terlambatColor), terlambatStartIndex, terlambatEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableTerlambatString.setSpan(StyleSpan(Typeface.BOLD), terlambatStartIndex, terlambatEndIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.tkr.text = spannableTerkirimString
        holder.tlt.text = spannableTerlambatString

        holder.cd.setOnClickListener {
            val frag = FragmentLaporan()

            val bundle = Bundle()
            bundle.putString("img", data.get("img").toString())
            bundle.putString("nm", data.get("nama").toString())
            bundle.putString("nip", data.get("nip").toString())
            bundle.putString("tkr", data.get("terkirim").toString())
            bundle.putString("tlt", data.get("telat").toString())
            bundle.putString("akt", data.get("terlambat").toString())
            frag.arguments = bundle

            frag.show((it.context as AppCompatActivity).supportFragmentManager, "")

        }
    }
}