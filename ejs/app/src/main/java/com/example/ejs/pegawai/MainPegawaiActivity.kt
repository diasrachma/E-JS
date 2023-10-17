package com.example.ejs.pegawai

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.BaseApplication
import com.example.ejs.ImageDetailActivity
import com.example.ejs.LoginActivity
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterBuktiMainPegawai
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_pegawai.*
import kotlinx.android.synthetic.main.nav_header.*
import org.json.JSONArray
import org.json.JSONObject

class MainPegawaiActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var urlClass: UrlClass
    val daftarBukti = mutableListOf<java.util.HashMap<String, String>>()
    lateinit var buktiAdapter: AdapterBuktiMainPegawai

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = "kosong"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_pegawai)
        supportActionBar?.setTitle("Home Pegawai")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        urlClass = UrlClass()

        lihatNomorPerkara()

        buktiAdapter = AdapterBuktiMainPegawai(daftarBukti, this)
        rvMainPegawai.layoutManager = LinearLayoutManager(this)
        rvMainPegawai.adapter = buktiAdapter

        btnProfilMainPegawai.setOnClickListener {
            val intent = Intent(this, UsersEditPegawaiActivity::class.java)
            startActivity(intent)
        }
        btnFormMainPegawai.setOnClickListener {
            val intent = Intent(this, FormPegawaiActivity::class.java)
            startActivity(intent)
        }
        btnRiwayatMainPegawai.setOnClickListener {
            val intent = Intent(this, BuktiRiwayatPegawaiActivity::class.java)
            startActivity(intent)
        }
        btnWA.setOnClickListener {
            WhatsApp()
        }

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayoutPegawai)
        val navView : NavigationView = findViewById(R.id.nav_viewPegawai)

        toggle = ActionBarDrawerToggle(this, drawerLayoutPegawai, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        showProfil("showProfil")
        dataBuktiPegawai("dataArsipUsers")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_homePegawai -> {
                    supportActionBar?.setTitle("Home Pegawai")
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_profilPegawai -> {
                    val intent = Intent(this, UsersEditPegawaiActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_formPegawai -> {
                    val intent = Intent(this, FormPegawaiActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_riwayatPegawai -> {
                    val intent = Intent(this, BuktiRiwayatPegawaiActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_chatPegawai -> {
                    WhatsApp()
                }
                R.id.nav_logoutPegawai -> {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.warning)
                        .setTitle("Logout")
                        .setMessage("Apakah Anda ingin keluar aplikasi?")
                        .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                            val prefEditor = preferences.edit()
                            prefEditor.putString(NIP,null)
                            prefEditor.commit()

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        })
                        .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                        })
                        .show()
                    true
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayoutPegawai.isDrawerOpen(GravityCompat.START)) {
            drawerLayoutPegawai.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun dataBuktiPegawai(mode: String) {
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
                val hm = java.util.HashMap<String, String>()
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

    fun WhatsApp() {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hello Admin")
                putExtra("jid", "${+62812-9054-3778}@s.whatsapp.net")
                type = "text/plain"
                setPackage("com.whatsapp")
            }
            startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            val appPackageName = "com.whatsapp"
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
        }
    }

    fun lihatNomorPerkara() {
        val request = object : StringRequest(
            Method.POST,urlClass.url_np,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                if (respon.equals("1")) {
                    BaseApplication.notificationHelper.showNpNotification(intent.getStringExtra("nama").toString())
                } else if (respon.equals("2")) {
                    Toast.makeText(this, "Tidak ada nomor perkara yang harus dikirim!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Tidak ada nomor perkara yang terlewat!", Toast.LENGTH_SHORT).show()
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nip", preferences.getString(NIP, DEF_NIP).toString())
                hm.put("mode", "notif_nomorperkara")

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_profil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val foto = jsonObject.getString("foto")

                tvNamaProfilMainPegawai.setText(nama)
                tvNipProfilMainPegawai.setText(nip)
                tvJabatanProfilMainPegawai.setText(jabatan)
                Picasso.get().load(foto).into(imgProfilMainPegawai)

                imgProfilMainPegawai.setOnClickListener {
                    val intent = Intent(this, ImageDetailActivity::class.java)
                    intent.putExtra("img", foto)
                    startActivity(intent)
                }

                usernameHeader.setText(nama)
                nipHeader.setText(nip)
                Picasso.get().load(foto).into(profilHeader)

                profilHeader.setOnClickListener {
                    val intent = Intent(this, ImageDetailActivity::class.java)
                    intent.putExtra("img", foto)
                    startActivity(intent)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nip", preferences.getString(NIP, DEF_NIP).toString())
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
}