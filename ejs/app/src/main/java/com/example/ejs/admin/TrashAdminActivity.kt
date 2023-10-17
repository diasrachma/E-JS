package com.example.ejs.admin

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterTrash
import kotlinx.android.synthetic.main.activity_trash.*
import org.json.JSONArray

class TrashAdminActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val daftarTrash = mutableListOf<HashMap<String,String>>()
    lateinit var trashAdapter: AdapterTrash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        urlClass = UrlClass()
        trashAdapter = AdapterTrash(daftarTrash)
        rvTrash.layoutManager = LinearLayoutManager( this)
        rvTrash.adapter = trashAdapter
    }

    override fun onStart() {
        super.onStart()
        showTrash("readAllTrash")
    }

    private fun showTrash(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_trash,
            Response.Listener { response ->
                daftarTrash.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("id_bukti",jsonObject.getString("id_bukti"))
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("tgl_bukti",jsonObject.getString("tgl_bukti"))
                        frm.put("tgl_hapus",jsonObject.getString("tgl_hapus"))

                        daftarTrash.add(frm)
                    }
                    trashAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "readAllTrash" -> {
                        hm.put("mode","readAllTrash")
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}