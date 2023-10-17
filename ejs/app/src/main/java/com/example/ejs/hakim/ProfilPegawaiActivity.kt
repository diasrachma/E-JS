package com.example.ejs.hakim

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profil.*
import org.json.JSONObject
import java.util.HashMap

class ProfilPegawaiActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        supportActionBar?.setTitle("Profil Pegawai")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()

        showProfil("show_profil")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_profil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val unitKerja = jsonObject.getString("unit_kerja")
                val masaKerja = jsonObject.getString("masa_kerja")
                val noHp = jsonObject.getString("telpon")
                val user = jsonObject.getString("level")
                val foto = jsonObject.getString("foto")

                if (user.equals("Pegawai")) {
                    rating("averageRating")
                } else {
                    tvPerforma.visibility = View.GONE
                    ratingBarProfil.visibility = View.GONE
                    tvRatingProfil.visibility = View.GONE
                }

                profilUnitKerja.setText(unitKerja)
                profilNama.setText(nama)
                profilNip.setText(nip)
                profilJabatan.setText(jabatan)
                profilGolongan.setText(golongan)
                profilMasaKerja.setText(masaKerja)
                profilNoHp.setText(noHp)
                profilLevel.setText(user)
                Picasso.get().load(foto).into(profilImage)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode){
                    "show_profil" -> {
                        hm.put("mode","showProfil")
                        hm.put("nip", paket?.getString("nip").toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun rating(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_profil,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val rating = jsonObject.getDouble("value").toFloat()
                    ratingBarProfil.rating = rating
                    tvRatingProfil.setText(rating.toString())
                } catch (e: Exception) {
                    Toast.makeText(this, "Gagal memuat rating", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode) {
                    "averageRating" -> {
                        hm.put("mode", "averageRating")
                        hm.put("nip",paket?.getString("nip").toString())
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}