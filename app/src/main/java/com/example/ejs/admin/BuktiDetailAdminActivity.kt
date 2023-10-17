package com.example.ejs.admin

import android.app.AlertDialog
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.ImageDetailActivity
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterKomentar
import com.example.ejs.hakim.RatingFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_bukti_detail.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BuktiDetailAdminActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val RC_IMAGE_SUKSES : Int=100
    val dataKomen = mutableListOf<HashMap<String,String>>()
    lateinit var komenAdapter: AdapterKomentar

    var terima_kd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bukti_detail)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()

        var paket : Bundle? = intent.extras
        detailId.setText(paket?.getString("idBukti"))
        terima_kd = paket?.getString("kdHakim").toString()

        komenAdapter = AdapterKomentar(dataKomen)
        rvKomentar.layoutManager = LinearLayoutManager(this)
        rvKomentar.adapter = komenAdapter

        if (terima_kd.equals("hakim")) {
            btnHapus.visibility = View.GONE
        } else {
            btnHapus.visibility = View.GONE
        }

        btnBeriRating.setOnClickListener {
            val dialog = RatingFragment()

            val bundle = Bundle()
            bundle.putString("id", paket?.getString("idBukti"))
            dialog.arguments = bundle
            dialog.show(supportFragmentManager, "KategoriEditFragment")
        }

        btnGetLocation.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Google Maps")
                .setMessage("Apakah Anda ingin mengakses Lokasi??")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    mapsLokasi("detailBukti")
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        btnHapus.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Hapus Data")
                .setMessage("Apakah Anda yakin ingin menghapus?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    deleteData("delete")
                    onBackPressed()
                    Toast.makeText(this, "Berhasil menghapus data!", Toast.LENGTH_LONG).show()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(this, "Anda membatalkan hapus data!", Toast.LENGTH_LONG).show()
                })
                .show()
        }

        btnPrevious.setOnClickListener {
            onBackPressed()
        }

        btnCopyLokasi.setOnClickListener {
            val textToCopy = detailLokasi.text
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Salin Lokasi ke Clipboard", Toast.LENGTH_LONG).show()
        }

        btnCopyAlamat.setOnClickListener {
            val textToCopy = detailAlamatFrag.text
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Salin Alamat ke Clipboard", Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteData(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                hm.put("id_bukti",paket?.getString("idBukti").toString())
                hm.put("nama",detailNama.text.toString())
                hm.put("tgl_bukti",detailJamTanggal.text.toString())
                when(mode) {
                    "delete" -> {
                        hm.put("mode", "delete")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun mapsLokasi(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val lokasi_bukti = jsonObject.getString("lokasi_bukti")

                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://www.google.co.id/maps/place/"+lokasi_bukti+"?z=25")
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                hm.put("id_bukti",paket?.getString("idBukti").toString())
                when(mode) {
                    "detailBukti" -> {
                        hm.put("mode", "detailBukti")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    override fun onStart() {
        super.onStart()
        buktiDetail("detailBukti")
        rating("rating")
        showKomen()
    }

    fun buktiDetail(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val jabatan = jsonObject.getString("jabatan")
                val foto_bukti = jsonObject.getString("url")
                val foto_relas = jsonObject.getString("url_relas")
                val vid = jsonObject.getString("video")
                val lokasi_bukti = jsonObject.getString("lokasi_bukti")
                val jamtanggal = jsonObject.getString("jamtanggal")
                val nomorperkara = jsonObject.getString("nomor_perkara")
                val alamat = jsonObject.getString("alamat")

                detailNama.setText(nama)
                detailJabatan.setText(jabatan)
                detailLokasi.setText(lokasi_bukti)
                detailJamTanggal.setText(jamtanggal)
                detailNomorPerkara.setText(nomorperkara)
                detailAlamatFrag.setText(alamat)
                Picasso.get().load(foto_bukti).into(imgDetailFoto)
                Picasso.get().load(foto_relas).into(imgDetailRelas)

                imgDetailFoto.setOnClickListener {
                    val intent = Intent(this, ImageDetailActivity::class.java)
                    intent.putExtra("img", foto_bukti)
                    startActivity(intent)
                }

                imgDetailRelas.setOnClickListener {
                    val intent = Intent(this, ImageDetailActivity::class.java)
                    intent.putExtra("img", foto_relas)
                    startActivity(intent)
                }

                val videoUri = Uri.parse(vid)
                videoDetail.setVideoURI(videoUri)
                videoDetail.setOnPreparedListener {
                    it.start()
                }

                var isPlaying = true
                videoDetail.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        if (isPlaying) {
                            videoDetail.pause()
                            isPlaying = false
                        } else {
                            videoDetail.start()
                            isPlaying = true
                        }
                    }
                    true
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                hm.put("id_bukti",paket?.getString("idBukti").toString())
                when(mode) {
                    "detailBukti" -> {
                        hm.put("mode", "detailBukti")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun showKomen() {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                dataKomen.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    var  bkt = java.util.HashMap<String, String>()
                    bkt.put("nama_komentar",jsonObject.getString("nama_komentar"))
                    bkt.put("teks_komentar",jsonObject.getString("teks_komentar"))

                    dataKomen.add(bkt)
                }
                komenAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                var paket : Bundle? = intent.extras
                hm.put("id_bukti",paket?.getString("idBukti").toString())
                hm.put("mode", "show_komen")

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun rating(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                try {
                    val jsonObject = JSONObject(response)
                    val rating = jsonObject.getDouble("value").toFloat()
                    ratingBar.rating = rating
                    tvRating.setText(rating.toString())
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
                    "rating" -> {
                        hm.put("mode", "rating")
                        hm.put("id_bukti",paket?.getString("idBukti").toString())
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}