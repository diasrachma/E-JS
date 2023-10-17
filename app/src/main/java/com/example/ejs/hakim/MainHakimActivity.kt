package com.example.ejs.hakim

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.ImageDetailActivity
import com.example.ejs.LoginActivity
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.admin.*
import com.example.ejs.pegawai.KendalaPegawaiActivity
import com.example.ejs.pegawai.UsersEditPegawaiActivity
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_hakim.*
import kotlinx.android.synthetic.main.nav_header.*
import org.json.JSONArray
import org.json.JSONObject

class MainHakimActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var urlClass: UrlClass

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = "kosong"

    var kd_hakim = "hakim"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_hakim)
        supportActionBar?.setTitle("Home Hakim")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        urlClass = UrlClass()

        imgEditProfil.setOnClickListener {
            val intent = Intent(this, UsersEditPegawaiActivity::class.java)
            startActivity(intent)
        }
        btnEditProfil.setOnClickListener {
            val intent = Intent(this, UsersEditPegawaiActivity::class.java)
            startActivity(intent)
        }

        imgMasterUserMenuHakim.setOnClickListener {
            val intent = Intent(this, UsersAdminActivity::class.java)
            intent.putExtra("kdHakim", kd_hakim)
            startActivity(intent)
        }
        btnMasterUserMenuHakim.setOnClickListener {
            val intent = Intent(this, UsersAdminActivity::class.java)
            intent.putExtra("kdHakim", kd_hakim)
            startActivity(intent)
        }

        imgRiwayatMenuHakim.setOnClickListener {
            val intent = Intent(this, ArsipAdminActivity::class.java)
            intent.putExtra("kdHakim", kd_hakim)
            startActivity(intent)
        }
        btnRiwayatMenuHakim.setOnClickListener {
            val intent = Intent(this, ArsipAdminActivity::class.java)
            intent.putExtra("kdHakim", kd_hakim)
            startActivity(intent)
        }

        imgNomorPerkaraMenuHakim.setOnClickListener {
            val intent = Intent(this, NomorPerkaraActivity::class.java)
            startActivity(intent)
        }
        btnNomorPerkaraMenuHakim.setOnClickListener {
            val intent = Intent(this, NomorPerkaraActivity::class.java)
            startActivity(intent)
        }

        imgTrashMenuHakim.setOnClickListener {
            val intent = Intent(this, TrashAdminActivity::class.java)
            startActivity(intent)
        }
        btnTrashMenuHakim.setOnClickListener {
            val intent = Intent(this, TrashAdminActivity::class.java)
            startActivity(intent)
        }

        showChart("chart")

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayoutHakim)
        val navView : NavigationView = findViewById(R.id.nav_viewHakim)

        toggle = ActionBarDrawerToggle(this, drawerLayoutHakim, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_homeHakim -> {
                    supportActionBar?.setTitle("Home Hakim")
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_profilHakim -> {
                    val intent = Intent(this, UsersEditPegawaiActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_npHakim -> {
                    val intent = Intent(this, NomorPerkaraActivity::class.java)
                    intent.putExtra("kdHakim", kd_hakim)
                    startActivity(intent)
                }
                R.id.nav_userHakim -> {
                    val intent = Intent(this, UsersAdminActivity::class.java)
                    intent.putExtra("kdHakim", kd_hakim)
                    startActivity(intent)
                }
                R.id.nav_riwayatHakim -> {
                    val intent = Intent(this, ArsipAdminActivity::class.java)
                    intent.putExtra("kdHakim", kd_hakim)
                    startActivity(intent)
                }
                R.id.nav_riwayatHapusHakim -> {
                    val intent = Intent(this, TrashAdminActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_monitoring -> {
                    val intent = Intent(this, LaporanActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_chatHakim -> {
                    WhatsApp()
                }
                R.id.nav_logoutHakim -> {
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
        if (drawerLayoutHakim.isDrawerOpen(GravityCompat.START)) {
            drawerLayoutHakim.closeDrawer(GravityCompat.START)
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

    override fun onStart() {
        super.onStart()
        showProfil("showProfil")
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

    private fun showChart(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                val entries = ArrayList<BarEntry>()
                val jsonArray = JSONArray(response)
                val labels = mutableListOf<String>()
                for (i in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val x = jsonObject.getString("nip")
                    val y = jsonObject.getInt("value")
                    entries.add(BarEntry(i.toFloat(), y.toFloat()))
                    labels.add(x)
                }

                setHorizontalBarChartData(entries, labels)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "chart" -> {
                        hm.put("mode","chart")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun setHorizontalBarChartData(entries: List<BarEntry>, labels: List<String>) {
        val dataSet = BarDataSet(entries, "Data")
        dataSet.setColors(*ColorTemplate.COLORFUL_COLORS)

        val barData = BarData(dataSet)
        horizontalBarChart.data = barData
        horizontalBarChart.description.isEnabled = false

        // Mengatur sumbu X dengan label yang sesuai
        val xAxis = horizontalBarChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        horizontalBarChart.animateX(1000)
        horizontalBarChart.invalidate()
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

                tvNamaProfilMainHakim.setText(nama)
                tvJabatanProfilMainHakim.setText(jabatan)
                tvNipProfilMainHakim.setText(nip)
                Picasso.get().load(foto).into(imgProfilMainHakim)

                imgProfilMainHakim.setOnClickListener {
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
                hm.put("nip",preferences.getString(NIP, DEF_NIP).toString())
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