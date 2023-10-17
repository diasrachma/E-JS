package com.example.ejs.admin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.ejs.pegawai.KendalaPegawaiActivity
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_admin.*
import kotlinx.android.synthetic.main.nav_header.*
import org.json.JSONObject

class MainAdminActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var urlClass: UrlClass

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = "kosong"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)
        supportActionBar?.setTitle("Home Admin")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        urlClass = UrlClass()

        imgRegistrasiMenuAdmin.setOnClickListener {
            val intent = Intent(this, RegistrasiActivity::class.java)
            startActivity(intent)
        }
        btnRegistrasiMenuAdmin.setOnClickListener {
            val intent = Intent(this, RegistrasiActivity::class.java)
            startActivity(intent)
        }

        imgMasterUserMenuAdmin.setOnClickListener {
            val intent = Intent(this, UsersAdminActivity::class.java)
            startActivity(intent)
        }
        btnMasterUserMenuAdmin.setOnClickListener {
            val intent = Intent(this, UsersAdminActivity::class.java)
            startActivity(intent)
        }

        imgRiwayatMenuAdmin.setOnClickListener {
            val intent = Intent(this, ArsipAdminActivity::class.java)
            startActivity(intent)
        }
        btnRiwayatMenuAdmin.setOnClickListener {
            val intent = Intent(this, ArsipAdminActivity::class.java)
            startActivity(intent)
        }

        imgNomorPerkaraMenuAdmin.setOnClickListener {
            val intent = Intent(this, NomorPerkaraActivity::class.java)
            startActivity(intent)
        }
        btnNomorPerkaraMenuAdmin.setOnClickListener {
            val intent = Intent(this, NomorPerkaraActivity::class.java)
            startActivity(intent)
        }

        imgTrashMenuAdmin.setOnClickListener {
            val intent = Intent(this, TrashAdminActivity::class.java)
            startActivity(intent)
        }
        btnTrashMenuAdmin.setOnClickListener {
            val intent = Intent(this, TrashAdminActivity::class.java)
            startActivity(intent)
        }

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayoutAdmin)
        val navView : NavigationView = findViewById(R.id.nav_viewAdmin)

        toggle = ActionBarDrawerToggle(this, drawerLayoutAdmin, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        showProfil("showProfil")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_homeAdmin -> {
                    supportActionBar?.setTitle("Home Admin")
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_npAdmin -> {
                    val intent = Intent(this, NomorPerkaraActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_registrasiAdmin -> {
                    val intent = Intent(this, RegistrasiActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_userAdmin -> {
                    val intent = Intent(this, UsersAdminActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_riwayatAdmin -> {
                    val intent = Intent(this, ArsipAdminActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_riwayatHapusAdmin -> {
                    val intent = Intent(this, TrashAdminActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_kendalaAdmin -> {
                    startActivity(Intent(this, KendalaPegawaiActivity::class.java))
                }
                R.id.nav_logoutAdmin -> {
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
        if (drawerLayoutAdmin.isDrawerOpen(GravityCompat.START)) {
            drawerLayoutAdmin.closeDrawer(GravityCompat.START)
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

    fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_profil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val foto = jsonObject.getString("foto")

                tvNamaProfilMainAdmin.setText(nama)
                tvJabatanProfilMainAdmin.setText(jabatan)
                tvNipProfilMainAdmin.setText(nip)
                Picasso.get().load(foto).into(imgProfilMainAdmin)

                imgProfilMainAdmin.setOnClickListener {
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