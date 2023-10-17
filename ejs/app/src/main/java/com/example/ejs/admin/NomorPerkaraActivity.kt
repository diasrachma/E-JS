package com.example.ejs.admin

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterNomorPerkara
import kotlinx.android.synthetic.main.activity_nomor_perkara.*
import org.json.JSONArray

class NomorPerkaraActivity : AppCompatActivity() {

    lateinit var urlClass : UrlClass

    val daftarNp = mutableListOf<HashMap<String,String>>()
    lateinit var npAdapter: AdapterNomorPerkara

    var terima_kd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nomor_perkara)
        supportActionBar?.setTitle("Nomor Perkara")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()
        npAdapter = AdapterNomorPerkara(daftarNp, this)
        rvNomorPerkara.layoutManager = LinearLayoutManager(this)
        rvNomorPerkara.adapter = npAdapter

        var paket : Bundle? = intent.extras
        terima_kd = paket?.getString("kdHakim").toString()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it?.itemId) {
                R.id.np_aktif -> {
                    frameLayout.visibility = View.GONE
                }
                R.id.np_non -> {
                    var frag = NomorTdkAktifFragment()

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, frag).commit()
                    frameLayout.setBackgroundColor(Color.argb(255,255,255,255))
                    frameLayout.visibility = View.VISIBLE
                }
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showNomorPerkara("readAllNp")
    }

    fun restartActivity() {
        recreate()
    }

    private fun showNomorPerkara(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_np,
            Response.Listener { response ->
                daftarNp.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("id_np",jsonObject.getString("id_np"))
                        frm.put("nama_np",jsonObject.getString("nama_np"))
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("status_np",jsonObject.getString("status_np"))

                        daftarNp.add(frm)
                    }
                    npAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "readAllNp" -> {
                        hm.put("mode","readAllNp")
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun deleteNp(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_np,
            Response.Listener { response ->

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("id_np",tvBayanganId.text.toString())
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
}