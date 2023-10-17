package com.example.ejs.admin

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.helper.MediaHelper
import kotlinx.android.synthetic.main.activity_registrasi.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class RegistrasiActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass
    lateinit var mediaHealper: MediaHelper
    var imStr = ""
    var levelText = ""

    val masaKerja = arrayOf(" ","1 Tahun","2 Tahun","3 Tahun", "4 Tahun", "5 Tahun", "6 Tahun", "7 Tahun", "8 Tahun",
        "9 Tahun", "10 Tahun", "11 Tahun", "12 Tahun", "13 Tahun", "14 Tahun", "15 Tahun", "16 Tahun", "17 Tahun", "18 Tahun", "19 Tahun", "20 Tahun", "> 20 tahun")

    lateinit var adapterMasaKerja: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        urlClass = UrlClass()
        mediaHealper = MediaHelper(this)

        adapterMasaKerja = ArrayAdapter(this, android.R.layout.simple_list_item_1,masaKerja)
        spMasaKerja.adapter = adapterMasaKerja

        insUnitKerja.setText("PENGADILAN NEGERI KAB KEDIRI")

        rgLevel.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.lvPegawa -> levelText = "Pegawai"
                R.id.lvlHakim -> levelText = "Hakim"
            }
        }

        btnInsertImage.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,mediaHealper.RcGallery())
        }

        btnKirimRegister.setOnClickListener {
            if (!insNip.text.toString().equals("") && !insNama.text.toString().equals("") && !insJabatan.text.toString().equals("")
                && !rgLevel.equals("")){
                registrasi("registrasiAkun")
                AlertDialog.Builder(this)
                    .setTitle("Notice!!")
                    .setMessage("Berhasil mendaftarkan akun baru")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        val intent = Intent(this, RegistrasiActivity::class.java)
                        startActivity(intent)
                        finish()
                    })
                    .show()
            } else {
                Toast.makeText(this,"Cek data kembali",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == mediaHealper.RcGallery()){
                imStr = mediaHealper.getBitmapToString(data!!.data,insImageProfil)
            }
        }
    }

    private fun registrasi(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_user,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                val nmFile ="IMG_"+ SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())+".jpg"
                hm.put("nama",insNama.text.toString())
                hm.put("nip",insNip.text.toString())
                hm.put("jabatan",insJabatan.text.toString())
                hm.put("golongan",insGolongan.text.toString())
                hm.put("unit_kerja",insUnitKerja.text.toString())
                hm.put("masa_kerja",spMasaKerja.selectedItem.toString())
                hm.put("telpon",insTelpon.text.toString())
                hm.put("level",levelText)
                hm.put("image",imStr)
                hm.put("file",nmFile)

                when(mode) {
                    "registrasiAkun" -> {
                        hm.put("mode", "registrasiAkun")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}
