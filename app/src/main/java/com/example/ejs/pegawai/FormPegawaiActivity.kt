package com.example.ejs.pegawai

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.MediaController
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.helper.PhotoHelper
import com.example.ejs.helper.VideoHelper
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import kotlinx.android.synthetic.main.activity_form.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class FormPegawaiActivity : AppCompatActivity() {

    private lateinit var notificationManager: NotificationManagerCompat

    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = "kosong"

    private lateinit var videoHelper: VideoHelper
    lateinit var photoHelper: PhotoHelper
    var imStr = ""
    var namaFile = ""
    var fileUri = Uri.parse("")

    var tahun = 0
    var bulan = 0
    var hari = 0
    var jam = 0
    var menit = 0
    var detik = 0
    var mildetik = 0

    var kd_maps = ""

    var vidStr = ""
    val REQUEST_CODE_PICK_VIDEO = 100

    var idBkt = ""
    private var nomorPerkara = ""

    private lateinit var mediaController: MediaController

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportActionBar?.setTitle("Form Jurusita")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        notificationManager = NotificationManagerCompat.from(this)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        photoHelper = PhotoHelper()
        videoHelper = VideoHelper(this)
        urlClass = UrlClass()

        mediaController = MediaController(this)
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView)

        getNomorPerkara("get_np")

        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val cal : Calendar = Calendar.getInstance()

        bulan = cal.get(Calendar.MONTH)+1
        hari = cal.get(Calendar.DAY_OF_MONTH)
        tahun = cal.get(Calendar.YEAR)
        jam = cal.get(Calendar.HOUR_OF_DAY)
        menit = cal.get(Calendar.MINUTE)
        detik = cal.get(Calendar.SECOND)
        mildetik = cal.get(Calendar.MILLISECOND)

        edtJamTanggal.setText("$tahun-$bulan-$hari $jam:$menit:$detik")

        edtKota.setText("Kab Kediri")

        var paket : Bundle? = intent.extras
        edtLokasi.setText(paket?.getString("maps"))
        edtAlamat.setText(paket?.getString("jln"))
        edtDesa.setText(paket?.getString("desa"))
        edtKecamatan.setText(paket?.getString("kec"))
        kd_maps = paket?.getString("kode").toString()

        if (kd_maps.equals("sukses")) {
            formMapsLokasi.visibility = View.VISIBLE
        }

        btnFormProfil.setOnClickListener {
            formProfilPegawai.visibility = View.VISIBLE
        }
        hideFormProfilPegawai.setOnClickListener {
            formProfilPegawai.visibility = View.GONE
        }

        btnMapsFormLokasi.setOnClickListener {
            formMapsLokasi.visibility = View.VISIBLE
        }
        hideFormMapsLokasi.setOnClickListener {
            formMapsLokasi.visibility = View.GONE
        }

        btnNomorPerkaraForm.setOnClickListener {
            formNomorPerkara.visibility = View.VISIBLE
        }
        hideFormNomorPerkara.setOnClickListener {
            formNomorPerkara.visibility = View.GONE
        }

        btnFormFotoEvidence.setOnClickListener {
            formFotoEvidence.visibility = View.VISIBLE
        }
        hideFormFotoEvidence.setOnClickListener {
            formFotoEvidence.visibility = View.GONE
        }

        btnFormVideoEvidence.setOnClickListener {
            formVideoEvidence.visibility = View.VISIBLE
        }
        btnCamera.setOnClickListener {
            requestPermission()
        }

        btnBukaMapsForm.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        btnUploadVideo.setOnClickListener {
            if (checkCameraPermission()) {
                startRecording()
            } else {
                requestCameraPermission()
            }
        }
        hideFormVideoEvidence.setOnClickListener {
            formVideoEvidence.visibility = View.GONE
        }

        btnFormKirimEvidence.setOnClickListener {
            progressForm.visibility = View.VISIBLE
            AlertDialog.Builder(this)
                .setTitle("Peringatan!")
                .setIcon(R.drawable.e_js_mobile)
                .setMessage("Apakah anda yakin ingin mengirim form Evidence?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    if (!edtLokasi.text.toString().equals("") && !edtDesa.text.toString().equals("") && !edtKecamatan.text.toString().equals("")) {
                        if (!nomorPerkara.equals("-- Pilih Nomor Perkara --")) {
                            if (!imStr.equals("") && !vidStr.equals("")) {
                                kirimEvidence("insert")
                            } else {
                                progressForm.visibility = View.GONE
                                Toast.makeText(this, "Foto atau Video tidak boleh kosong", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            progressForm.visibility = View.GONE
                            Toast.makeText(this, "Tolong pilih nomor perkara terlebih dahulu!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        progressForm.visibility = View.GONE
                        Toast.makeText(this, "Tolong masukkan alamat terlebih dahulu!", Toast.LENGTH_SHORT).show()
                    }
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(this, "Membatalkan pengiriman Bukti", Toast.LENGTH_LONG).show()
                })
                .show()
        }
    }

    private fun checkCameraPermission(): Boolean {
        val permission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        val permission = arrayOf(Manifest.permission.CAMERA)
        ActivityCompat.requestPermissions(this, permission, VideoHelper.RC_CAMERA)
    }

    private fun startRecording() {
        videoHelper.startRecordingVideo()
    }

    fun requestPermission() = runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA ) {
        fileUri = photoHelper.getOutputMediaFileUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(intent, photoHelper.getRcCamera())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            photoHelper.getRcCamera() -> {
                when (resultCode) {
                    RESULT_OK -> {
                        imStr = photoHelper.getBitMapToString(imgBukti, fileUri)
                        namaFile = photoHelper.getMyFileName()
                        Toast.makeText(this, "Berhasil upload foto", Toast.LENGTH_SHORT).show()
                    }
                    RESULT_CANCELED -> {
                        // kode untuk kondisi kedua jika dibatalkan
                    }
                }
            }
            VideoHelper.RC_CAMERA -> {
                when (resultCode) {
                    RESULT_OK -> {
                        vidStr = videoHelper.encodeVideoToBase64()
                        val videoUri = videoHelper.getVideoFileUri()
                        if (videoUri != null) {
                            videoView.setVideoURI(videoUri)
                            videoView.setOnPreparedListener(MediaPlayer.OnPreparedListener { mediaPlayer ->
                                mediaPlayer.isLooping = true
                                mediaPlayer.start()
                            })
                        }
                        Toast.makeText(this, "Berhasil upload video", Toast.LENGTH_SHORT).show()
                    }
                    RESULT_CANCELED -> {

                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        profilUser("showProfil")
    }

    private fun getNomorPerkara(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_np,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                val dataList = mutableListOf<HashMap<String, String>>()

                val defaultItem = HashMap<String, String>()
                defaultItem["nama_np"] = "-- Pilih Nomor Perkara --"
                defaultItem["tanggal_np"] = "*"
                dataList.add(defaultItem)

                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val namaNp = jsonObject.getString("nama_np")
                    val tglNp = jsonObject.getString("tanggal_np")

                    val item = HashMap<String, String>()
                    item["nama_np"] = namaNp
                    item["tanggal_np"] = tglNp
                    dataList.add(item)
                }

                val adapter = SimpleAdapter(
                    this,
                    dataList,
                    android.R.layout.simple_expandable_list_item_2,
                    arrayOf("nama_np", "tanggal_np"),
                    intArrayOf(android.R.id.text1, android.R.id.text2)
                )
                spFormNomorPerkaraPegawai.adapter = adapter

                spFormNomorPerkaraPegawai.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        // Ketika item dipilih dari Spinner, Anda dapat mengakses data yang dipilih dari dataList
                        val selectedItem = dataList[position]
                        val namaNp = selectedItem["nama_np"]
                        val tglNp = selectedItem["tanggal_np"]

                        nomorPerkara = namaNp.toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Tidak ada item yang dipilih
                    }
                }
            },
            Response.ErrorListener { error ->

            }) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                when (mode) {
                    "get_np" -> {
                        hm.put("mode", "get_np")
                        hm.put("nip", preferences.getString(NIP,DEF_NIP).toString())
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun profilUser(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_profil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val unitKerja = jsonObject.getString("unit_kerja")
                val telpon = jsonObject.getString("telpon")

                txNamaFormPegawai.setText(nama)
                txNipFormPegawai.setText(nip)
                txUnitKerjaFormPegawai.setText(unitKerja)
                txTelponFormPegawai.setText(telpon)
                txJabatanFormPegawai.setText(jabatan)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nip",preferences.getString(NIP, DEF_NIP).toString())
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

    private fun kirimEvidence(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                val idBukti = jsonObject.getString("idBukti")
                if(respon.equals("0")){
                    Toast.makeText(this,"Gagal Mengirim Form Evidence", Toast.LENGTH_LONG).show()
                }else{
                    idBkt = idBukti
                    val intent = Intent(this, FormRelasPegawaiActivity::class.java)
                    intent.putExtra("idBukti", idBkt)
                    intent.putExtra("nama", txNamaFormPegawai.text.toString())
                    startActivity(intent)
                    Toast.makeText(this,"Berhasil Mengirim Form Evidence", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                val nmVideo ="VID_"+ SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(
                    Date()
                )+".mp4"

                when(mode) {
                    "insert" -> {
                        hm.put("mode", "insert")
                        hm.put("nip",preferences.getString(NIP,DEF_NIP).toString())
                        hm.put("lokasi_bukti",edtLokasi.text.toString())
                        hm.put("jamtanggal",edtJamTanggal.text.toString())
                        hm.put("nomor_perkara", nomorPerkara)
                        hm.put("alamat_bukti",edtAlamat.text.toString())
                        hm.put("desa_bukti",edtDesa.text.toString())
                        hm.put("kecamatan_bukti",edtKecamatan.text.toString())
                        hm.put("kota_bukti",edtKota.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",namaFile)
                        hm.put("vid",vidStr)
                        hm.put("fileVideo",nmVideo)
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}