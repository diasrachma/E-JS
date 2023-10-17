package com.example.ejs.hakim

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.ejs.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_laporan.view.*

class FragmentLaporan : DialogFragment() {
    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_laporan, container, false)

        val img = arguments?.get("img").toString()
        val nm = arguments?.get("nm").toString()
        val tkr = arguments?.get("tkr").toString()
        val akt = arguments?.get("akt").toString()
        val tlt = arguments?.get("tlt").toString()

        Picasso.get().load(img).into(v.imgJurusita)
        v.laporanNama.setText(nm)
        v.laporanTerkirim.text = tkr
        v.laporanNomorPerkara.text = akt
        v.laporanTelatKirim.text = tlt

        v.btnLihatMonitoring.setOnClickListener {
            val intent = Intent(v.context, LaporanDetailActivity::class.java)
            intent.putExtra("nip", arguments?.get("nip").toString())
            v.context.startActivity(intent)
        }

        return v
    }
}