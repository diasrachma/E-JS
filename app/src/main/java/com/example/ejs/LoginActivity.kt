package com.example.ejs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.hakim.MainHakimActivity
import com.example.ejs.pegawai.MainPegawaiActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var preferences: SharedPreferences
    lateinit var urlClass: UrlClass
    lateinit var nipAdapter: ArrayAdapter<String>
    val daftarNip = mutableListOf<String>()

    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        switchTema.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES )
            else
                AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO )
        }

        nipAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,daftarNip)
        etNipLogin.setAdapter(nipAdapter)
        etNipLogin.threshold = 0

        urlClass = UrlClass()

        btnLogin.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            if (!etNipLogin.text.toString().equals("")){
                validationAccount("login")
            }else{
                Toast.makeText(this,"NIP Tidak boleh kosong!", Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        progressBar.visibility = View.GONE
        getNip("getNip")
    }

    fun validationAccount(mode: String){
        val request = object : StringRequest(Method.POST,urlClass.login,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val level = jsonObject.getString("level")
                val statusAkun = jsonObject.getString("sts_akun")
                if(level.equals("Pegawai") && statusAkun.equals("AKTIF")){
                    preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    val prefEditor = preferences.edit()
                    prefEditor.putString(NIP,etNipLogin.text.toString())
                    prefEditor.putString("foto",jsonObject.toString())
                    prefEditor.commit()

                    val nama = jsonObject.getString("nama")
                    val intent = Intent(this, MainPegawaiActivity::class.java)
                    intent.putExtra("nama",nama)
                    intent.putExtra("level",level)
                    startActivity(intent)
                    finish()
                }else if(level.equals("Admin") && statusAkun.equals("AKTIF")){
                    preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    val prefEditor = preferences.edit()
                    prefEditor.putString(NIP,etNipLogin.text.toString())
                    prefEditor.putString("foto",jsonObject.toString())
                    prefEditor.commit()
                    AlertDialog.Builder(this)
                        .setTitle("Peringatan!")
                        .setMessage("Admin hanya bisa di akses melalui Website!")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            progressBar.visibility = View.GONE
                        })
                        .show()
                }else if(level.equals("Hakim") && statusAkun.equals("AKTIF")) {
                    preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    val prefEditor = preferences.edit()
                    prefEditor.putString(NIP, etNipLogin.text.toString())
                    prefEditor.putString("foto", jsonObject.toString())
                    prefEditor.commit()

                    val nama = jsonObject.getString("nama")
                    val intent = Intent(this, MainHakimActivity::class.java)
                    intent.putExtra("nama", nama)
                    startActivity(intent)
                    finish()
                }else if(statusAkun.equals("NON")){
                    AlertDialog.Builder(this)
                        .setTitle("Peringatan!")
                        .setMessage("Status Akun Anda Telah Dinon-aktifkan!")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            progressBar.visibility = View.GONE
                        })
                        .show()
                }else{
                    AlertDialog.Builder(this)
                        .setTitle("Peringatan!")
                        .setMessage("NIP yang Anda masukkan salah!")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            progressBar.visibility = View.GONE
                        })
                        .show()
                }
            },
            Response.ErrorListener { error ->
                AlertDialog.Builder(this)
                    .setTitle("Peringatan!")
                    .setMessage("Tidak dapat terhubung ke server")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        progressBar.visibility = View.GONE
                    })
                    .show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nip",etNipLogin.text.toString())
                when(mode) {
                    "login" -> {
                        hm.put("mode", "login")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun getNip(mode: String) {
        val request = object : StringRequest(
            Request.Method.POST,urlClass.login,
            Response.Listener { response ->
                daftarNip.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarNip.add(jsonObject.getString("nip"))
                }
                nipAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->

            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode) {
                    "getNip" -> {
                        hm.put("mode", "getNip")
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}