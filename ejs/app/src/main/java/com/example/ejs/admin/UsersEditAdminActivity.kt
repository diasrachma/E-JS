package com.example.ejs.admin

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
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
import kotlinx.android.synthetic.main.row_users.masakerjaPegawai
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class UsersEditAdminActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass
    lateinit var mediaHealper: MediaHelper
    var imStr = ""

    val masaKerja = arrayOf(" ","1 Tahun","2 Tahun","3 Tahun", "4 Tahun", "5 Tahun", "6 Tahun", "7 Tahun", "8 Tahun",
        "9 Tahun", "10 Tahun", "11 Tahun", "12 Tahun", "13 Tahun", "14 Tahun", "15 Tahun", "16 Tahun", "17 Tahun", "18 Tahun", "19 Tahun", "20 Tahun", "> 20 Tahun")

    lateinit var adapterMasaKerja: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_edit)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        urlClass = UrlClass()

        mediaHealper = MediaHelper(this)

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
            alertBuilder.setTitle("Informasi!").setMessage("Yakin ingin merubah data profil users " + edtNamaProfil.text.toString() +" ?")
            alertBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                updateDataProfil("updateProfil")
                val intent = Intent(this, UsersAdminActivity::class.java)
                startActivity(intent)
            })
            alertBuilder.setNegativeButton("Batal",null)
            alertBuilder.show()
        }
    }

    override fun onStart() {
        super.onStart()
        showProfil("showProfil")
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
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val telpon = jsonObject.getString("telpon")
                val unitKerja = jsonObject.getString("unit_kerja")
                val level = jsonObject.getString("level")
                val foto = jsonObject.getString("foto")

                edtNamaProfil.setText(nama)
                edtNipProfil.setText(nip)
                edtJabatanProfil.setText(jabatan)
                edtGolonganProfil.setText(golongan)
                edtTelponProfil.setText(telpon)
                edtLevelProfil.setText(level)
                edtUnitKerjaProfil.setText(unitKerja)
                Picasso.get().load(foto).into(edtImageProfil)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                hm.put("nip",paket?.getString("nip_pegawai").toString())
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
                var paket : Bundle? = intent.extras
                hm.put("nip",paket?.getString("nip_pegawai").toString())
                hm.put("nama",edtNamaProfil.text.toString())
                hm.put("jabatan",edtJabatanProfil.text.toString())
                hm.put("golongan",edtGolonganProfil.text.toString())
                hm.put("unit_kerja",edtUnitKerjaProfil.text.toString())
                hm.put("masa_kerja", masakerjaPegawai.text.toString())
                hm.put("telpon",edtTelponProfil.text.toString())
                hm.put("level",edtLevelProfil.text.toString())
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