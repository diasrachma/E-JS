package com.example.ejs.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterUsersArsipAdmin
import kotlinx.android.synthetic.main.activity_users_arsip.*
import org.json.JSONArray

class UsersArsipAdminActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass

    val daftarBukti = mutableListOf<HashMap<String,String>>()
    lateinit var buktiAdapter: AdapterUsersArsipAdmin
    var terima_kd = ""

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = "kosong"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_arsip)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        urlClass = UrlClass()

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        refreshUp()

        buktiAdapter = AdapterUsersArsipAdmin(daftarBukti, this)
        rvBuktiPerpegawai.layoutManager = LinearLayoutManager(this)
        rvBuktiPerpegawai.adapter = buktiAdapter
    }

    private fun refreshUp() {
        refreshPerpegawai.setOnRefreshListener {
            Handler().postDelayed(Runnable {
                dataBuktiPerpegawai("dataArsipUsers")
                refreshPerpegawai.isRefreshing = false
            }, 2000)
        }
    }

    override fun onStart() {
        super.onStart()
        dataBuktiPerpegawai("dataArsipUsers")
    }

    private fun dataBuktiPerpegawai(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                daftarBukti.clear()
                val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  bkt = java.util.HashMap<String, String>()
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
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                when(mode) {
                    "dataArsipUsers" -> {
                        hm.put("mode", "dataArsipUsers")
                        hm.put("nip", intent?.getStringExtra("nip").toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}