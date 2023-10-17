package com.example.ejs.pegawai

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterBuktiPegawai
import kotlinx.android.synthetic.main.activity_bukti_riwayat.*
import org.json.JSONArray
import java.util.HashMap

class BuktiRiwayatPegawaiActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val daftarBukti = mutableListOf<HashMap<String, String>>()
    lateinit var buktiAdapter: AdapterBuktiPegawai

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = "kosong"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bukti_riwayat)
        supportActionBar?.setTitle("RIWAYAT")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        urlClass = UrlClass()

        buktiAdapter = AdapterBuktiPegawai(daftarBukti, this)
        rvBuktiPegawai.layoutManager = LinearLayoutManager(this)
        rvBuktiPegawai.adapter = buktiAdapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        dataBuktiPegawai("dataArsipUsers")
    }

    private fun dataBuktiPegawai(mode: String) {
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
                    bkt.put("nomor_perkara",jsonObject.getString("nomor_perkara"))
                    bkt.put("url",jsonObject.getString("url"))
                    bkt.put("nip",preferences.getString(NIP,DEF_NIP).toString())

                    daftarBukti.add(bkt)
                }
                buktiAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nip",preferences.getString(NIP,DEF_NIP).toString())
                when(mode) {
                    "dataArsipUsers" -> {
                        hm.put("mode", "dataArsipUsers")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}