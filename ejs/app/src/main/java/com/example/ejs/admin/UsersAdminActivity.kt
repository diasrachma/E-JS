package com.example.ejs.admin

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
import com.example.ejs.adapter.AdapterUsers
import kotlinx.android.synthetic.main.activity_users.*
import org.json.JSONArray

class UsersAdminActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass

    val daftarUsers = mutableListOf<HashMap<String,String>>()
    lateinit var usersAdapter : AdapterUsers

    var terima_kd = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        supportActionBar?.setTitle("Master Users")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()

        var paket : Bundle? = intent.extras
        terima_kd = paket?.getString("kdHakim").toString()

        usersAdapter = AdapterUsers(daftarUsers, this)
        rvPegawai.layoutManager = LinearLayoutManager(this)
        rvPegawai.adapter = usersAdapter

        showDetailUsers("readAllUser","")

        btnSearch.setOnClickListener {
            showDetailUsers("readAllUser", textSearch.text.toString().trim())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showDetailUsers(mode:String, nama : String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_user,
            Response.Listener { response ->
                daftarUsers.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    var  usr = HashMap<String,String>()
                    usr.put("nip",jsonObject.getString("nip"))
                    usr.put("nama",jsonObject.getString("nama"))
                    usr.put("jabatan",jsonObject.getString("jabatan"))
                    usr.put("golongan",jsonObject.getString("golongan"))
                    usr.put("unit_kerja",jsonObject.getString("unit_kerja"))
                    usr.put("masa_kerja",jsonObject.getString("masa_kerja"))
                    usr.put("sts_akun",jsonObject.getString("sts_akun"))
                    usr.put("foto",jsonObject.getString("foto"))
                    usr.put("level",jsonObject.getString("level"))
                    daftarUsers.add(usr)
                }
                usersAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Terjadi kesalahan koneksi ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode) {
                    "readAllUser" -> {
                        hm.put("mode", "readAllUser")
                        hm.put("nama", nama)
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun deletePegawai(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_user,
            Response.Listener { response ->

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nip",tvNipHapus.text.toString())
                when(mode) {
                    "non_aktif" -> {
                        hm.put("mode", "non_aktif")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun aktifkanPegawai(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_user,
            Response.Listener { response ->

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nip",tvNipHapus.text.toString())
                when(mode) {
                    "aktif" -> {
                        hm.put("mode", "aktif")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

}