package com.example.ejs.pegawai

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterKendala
import kotlinx.android.synthetic.main.activity_kendala.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class KendalaPegawaiActivity : AppCompatActivity() {

    val daftarKendala = mutableListOf<HashMap<String,String>>()
    lateinit var kendalaAdapter: AdapterKendala

    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = "kosong"

    var teks = ""
    var idK = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kendala)
        supportActionBar?.setTitle("KENDALA")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        urlClass = UrlClass()

        kendalaAdapter = AdapterKendala(daftarKendala, this)
        rvKendala.layoutManager = LinearLayoutManager(this)
        rvKendala.adapter = kendalaAdapter
        rvKendala.scrollToPosition(kendalaAdapter.itemCount - 1)

        btnKirimKendala.setOnClickListener {
            progressBarKendala.visibility = View.VISIBLE
            teks = txChatKendala.text.toString()
            kirimPesan("insert")
            txChatKendala.setText("")
            restartActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        dataKendala("readKendala")
        progressBarKendala.visibility = View.GONE
    }

    fun restartActivity() {
        recreate()
        txChatKendala.clearFocus()
    }

    private fun dataKendala(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_kendala,
            Response.Listener { response ->
                daftarKendala.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    var  bkt = java.util.HashMap<String, String>()
                    bkt.put("id_kendala",jsonObject.getString("id_kendala"))
                    bkt.put("pesan",jsonObject.getString("pesan"))
                    bkt.put("nama",jsonObject.getString("nama"))
                    bkt.put("level",jsonObject.getString("level"))
                    bkt.put("jam_kendala",jsonObject.getString("jam_kendala"))
                    bkt.put("foto",jsonObject.getString("foto"))

                    daftarKendala.add(bkt)
                }
                kendalaAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                when(mode) {
                    "readKendala" -> {
                        hm.put("mode", "readKendala")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun delete(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_kendala,
            Response.Listener { response ->

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode) {
                    "delete" -> {
                        hm.put("mode", "delete")
                        hm.put("id_kendala", idK)
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun kirimPesan(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_kendala,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                when(mode) {
                    "insert" -> {
                        hm.put("mode", "insert")
                        hm.put("nip",preferences.getString(NIP,DEF_NIP).toString())
                        hm.put("pesan", teks)
                        hm.put("jam_kendala", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
                    }
                }
                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}