package com.example.ejs.pegawai

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.BaseApplication
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.helper.PhotoHelper
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_form_relas.*
import org.json.JSONObject

class FormRelasPegawaiActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManagerCompat

    lateinit var urlClass: UrlClass
    lateinit var photoHelper: PhotoHelper
    var imStr = ""
    var namaFile = ""
    var fileUri = Uri.parse("")

    var nm = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_relas)
        supportActionBar?.setTitle("Form Jurusita")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        notificationManager = NotificationManagerCompat.from(this)

        photoHelper = PhotoHelper()
        urlClass = UrlClass()

        var paket : Bundle? = intent.extras
        nm = paket?.getString("nama").toString()
        tvIdBuktiRelas.setText(paket?.getString("idBukti"))

        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        btnFormDokumenEvidence.setOnClickListener {
            formDokumenEvidence.visibility = View.VISIBLE
        }
        hideFormDokumenRelas.setOnClickListener {
            formDokumenEvidence.visibility = View.GONE
        }
        btnCameraDokumen.setOnClickListener {
            requestPermission()
        }

        btnFormKirimRelas.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Peringatan!")
                .setIcon(R.drawable.e_js_mobile)
                .setMessage("Apakah anda yakin ingin mengirim form Evidence?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    kirimRelas("foto_relas")
                    val intent = Intent(this, MainPegawaiActivity::class.java)
                    startActivity(intent)
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(this, "Membatalkan pengiriman Bukti", Toast.LENGTH_LONG).show()
                })
                .show()
        }
    }

    fun requestPermission() = runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA ) {
        fileUri = photoHelper.getOutputMediaFileUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(intent, photoHelper.getRcCamera())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == photoHelper.getRcCamera() && resultCode == RESULT_OK) {
            imStr = photoHelper.getBitMapToString(imgDokumenRelas, fileUri)
            namaFile = photoHelper.getMyFileName()
            Toast.makeText(this, "Berhasil upload foto", Toast.LENGTH_SHORT).show()
        }
    }

//    fun Notification() {
//        val tittle = nm
//        val message = "Berhasil Upload Form Evidence!"
//        val builder = NotificationCompat.Builder(this, BaseApplication.CHANNEL_1_ID)
//            .setSmallIcon(android.R.drawable.stat_notify_chat)
//            .setContentTitle(tittle)
//            .setContentText(message)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//
//        val notification = builder.build()
//        notificationManager.notify(1, notification)
//    }

    fun kirimRelas(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                if(respon.equals("0")){
                    Toast.makeText(this,"Gagal Mengirim Form Evidence", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this,"Berhasil Mengirim Form Evidence", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
//                var paket : Bundle? = intent.extras
//                idBukti = paket?.getString("idBukti").toString()

                hm.put("id_bukti", tvIdBuktiRelas.text.toString())
                hm.put("image",imStr)
                hm.put("file",namaFile)
                when(mode) {
                    "foto_relas" -> {
                        hm.put("mode", "foto_relas")
                    }
                }
                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}