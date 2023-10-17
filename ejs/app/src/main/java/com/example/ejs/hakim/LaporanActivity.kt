package com.example.ejs.hakim

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterLaporan
import kotlinx.android.synthetic.main.activity_laporan.*
import org.json.JSONArray

class LaporanActivity : AppCompatActivity() {

    val daftarBukti = mutableListOf<HashMap<String,String>>()
    lateinit var buktiAdapter: AdapterLaporan

    private var urlClass: UrlClass = UrlClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan)
        supportActionBar?.setTitle("Laporan Monitoring")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buktiAdapter = AdapterLaporan(daftarBukti)
        rvLaporan.layoutManager = LinearLayoutManager(this)
        rvLaporan.adapter = buktiAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showDataBukti("")
    }

    private fun showDataBukti(nama: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_laporan,
            Response.Listener { response ->
                daftarBukti.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    var  bkt = HashMap<String,String>()
                    bkt.put("nama",jsonObject.getString("nama"))
                    bkt.put("nip",jsonObject.getString("nip"))
                    bkt.put("terkirim",jsonObject.getString("terkirim"))
                    bkt.put("telat",jsonObject.getString("telat"))
                    bkt.put("terlambat",jsonObject.getString("terlambat"))
                    bkt.put("img",jsonObject.getString("img"))
                    daftarBukti.add(bkt)
                }
                buktiAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("mode", "get_laporan")
                hm.put("nomor_perkara", nama)

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}