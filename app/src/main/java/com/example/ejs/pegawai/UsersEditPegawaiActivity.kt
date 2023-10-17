package com.example.ejs.pegawai

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.helper.MediaHelper
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_users_edit.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class UsersEditPegawaiActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = "kosong"

    lateinit var urlClass: UrlClass
    lateinit var mediaHealper: MediaHelper
    var imStr = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_edit)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        urlClass = UrlClass()

        edtNipProfil.setText(preferences.getString(NIP,DEF_NIP).toString())

        mediaHealper = MediaHelper(this)

        btnEditProfilUser.setOnClickListener {
            tx1.setText("EDIT PROFIL")
            btnEditImageProfil.visibility = View.VISIBLE
            btnSimpanProfilUser.visibility = View.VISIBLE
            edtNamaProfil.isEnabled = true
            edtTelponProfil.isEnabled = true
            edtLevelProfil.isEnabled = true
            materialTvMasaKerjaProfil.visibility = View.GONE
            btnBatalProfilUser.visibility = View.VISIBLE
            tvMasaKerjaProfil.visibility = View.GONE
            btnEditProfilUser.visibility = View.GONE
            tvPerforma.visibility = View.GONE
            cardRating.visibility = View.GONE
        }

        btnBatalProfilUser.setOnClickListener {
            tx1.setText("PROFIL")
            btnEditImageProfil.visibility = View.GONE
            btnSimpanProfilUser.visibility = View.GONE
            edtNamaProfil.isEnabled = false
            edtTelponProfil.isEnabled = false
            edtLevelProfil.isEnabled = false
            materialTvMasaKerjaProfil.visibility = View.VISIBLE
            tvMasaKerjaProfil.visibility = View.VISIBLE
            btnBatalProfilUser.visibility = View.GONE
            btnEditProfilUser.visibility = View.VISIBLE
            tvPerforma.visibility = View.VISIBLE
            cardRating.visibility = View.VISIBLE
        }

        btnEditImageProfil.setOnClickListener {
            edtImageProfil.visibility = View.GONE
            edtImageProfilUpdate.visibility = View.VISIBLE
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,mediaHealper.RcGallery())
        }

        btnSimpanProfilUser.setOnClickListener {
            var alertBuilder = AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
            alertBuilder.setTitle("Informasi!").setMessage("Yakin ingin merubah data profil Anda?")
            alertBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                updateDataProfil("updateProfil")
                recreate()
            })
            alertBuilder.setNegativeButton("Batal",null)
            alertBuilder.show()
        }
    }

    override fun onStart() {
        super.onStart()
        showProfil("showProfil")
        rating("averageRating")
        btnEditImageProfil.visibility = View.GONE
        btnSimpanProfilUser.visibility = View.GONE
        edtNamaProfil.isEnabled = false
        edtJabatanProfil.isEnabled = false
        edtGolonganProfil.isEnabled = false
        edtTelponProfil.isEnabled = false
        edtLevelProfil.isEnabled = false
        tvMasaKerjaProfil.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == mediaHealper.RcGallery()){
                imStr = mediaHealper.getBitmapToString(data!!.data,edtImageProfilUpdate)
            }
        }
    }

    private fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_profil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val telpon = jsonObject.getString("telpon")
                val unitKerja = jsonObject.getString("unit_kerja")
                val masaKerja = jsonObject.getString("masa_kerja")
                val level = jsonObject.getString("level")
                val foto = jsonObject.getString("foto")

                edtNamaProfil.setText(nama)
                edtJabatanProfil.setText(jabatan)
                edtGolonganProfil.setText(golongan)
                edtTelponProfil.setText(telpon)
                edtLevelProfil.setText(level)
                tvMasaKerjaProfil.setText(masaKerja)
                edtUnitKerjaProfil.setText(unitKerja)
                Picasso.get().load(foto).into(edtImageProfil)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nip",preferences.getString(NIP,DEF_NIP).toString())
                when(mode) {
                    "showProfil" -> {
                        hm.put("mode", "showProfil")
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
                        hm.put("nip", preferences.getString(NIP,DEF_NIP).toString())
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun updateDataProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_profil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if(respon.equals("1")){
                    Toast.makeText(this,"Berhasil merubah profil "+edtNamaProfil.text.toString(),Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Gagal merubah profil",Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                val nmFile ="IMG_"+ SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())+".jpg"
                hm.put("nip",preferences.getString(NIP,DEF_NIP).toString())
                hm.put("nama",edtNamaProfil.text.toString())
                hm.put("jabatan",edtJabatanProfil.text.toString())
                hm.put("golongan",edtGolonganProfil.text.toString())
                hm.put("unit_kerja",edtUnitKerjaProfil.text.toString())
                hm.put("masa_kerja", tvMasaKerjaProfil.text.toString())
                hm.put("telpon",edtTelponProfil.text.toString())
                hm.put("image",imStr)
                hm.put("file",nmFile)

                when(mode) {
                    "updateProfil" -> {
                        hm.put("mode", "updateProfil")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}