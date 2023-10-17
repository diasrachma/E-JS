package com.example.ejs.admin

import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterBuktiAdmin
import kotlinx.android.synthetic.main.activity_arsip.*
import org.json.JSONArray

class ArsipAdminActivity : AppCompatActivity() {

    val daftarBukti = mutableListOf<HashMap<String,String>>()
    lateinit var buktiAdapter: AdapterBuktiAdmin

    lateinit var urlClass: UrlClass
    var terima_kd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arsip)
        supportActionBar?.setTitle("Arsip")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()

        var paket : Bundle? = intent.extras
        terima_kd = paket?.getString("kdHakim").toString()

        buktiAdapter = AdapterBuktiAdmin(daftarBukti, this)
        rvBukti.layoutManager = LinearLayoutManager(this)
        rvBukti.adapter = buktiAdapter

        btnSearch.setOnClickListener {
            showDataBukti("showDataBukti",textSearch.text.toString().trim())
        }

        refreshUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showDataBukti("showDataBukti", "")
    }

    private fun refreshUp(){
        refresh.setOnRefreshListener {
            Handler().postDelayed(Runnable {
                showDataBukti("showDataBukti","")
                refresh.isRefreshing = false
            }, 2000)
        }
    }

    private fun showDataBukti(mode: String, nomor_perkara : String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                daftarBukti.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    var  bkt = HashMap<String,String>()
                    bkt.put("id_bukti",jsonObject.getString("id_bukti"))
                    bkt.put("jamtanggal",jsonObject.getString("jamtanggal"))
                    bkt.put("nama",jsonObject.getString("nama"))
                    bkt.put("nomor_perkara",jsonObject.getString("nomor_perkara"))
                    bkt.put("jabatan",jsonObject.getString("jabatan"))
                    bkt.put("url",jsonObject.getString("url"))
                    daftarBukti.add(bkt)
                }
                buktiAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nomor_perkara",nomor_perkara)
                when(mode) {
                    "showDataBukti" -> {
                        hm.put("mode", "showDataBukti")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

}
