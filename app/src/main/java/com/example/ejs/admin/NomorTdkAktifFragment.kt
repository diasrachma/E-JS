package com.example.ejs.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterNomorPerkara
import kotlinx.android.synthetic.main.fragment_nomor_tdk_aktif.view.*
import org.json.JSONArray

class NomorTdkAktifFragment : Fragment() {
    lateinit var thisParent: NomorPerkaraActivity
    lateinit var v: View

    lateinit var urlClass : UrlClass

    val daftarNp = mutableListOf<HashMap<String,String>>()
    lateinit var npAdapter: AdapterNomorPerkara

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as NomorPerkaraActivity
        v = inflater.inflate(R.layout.fragment_nomor_tdk_aktif, container, false)

        urlClass = UrlClass()
        npAdapter = AdapterNomorPerkara(daftarNp, thisParent)
        v.rvNomorPerkara.layoutManager = LinearLayoutManager(this.context)
        v.rvNomorPerkara.adapter = npAdapter

        showNomorPerkara("readAllNp")

        return v
    }

    private fun showNomorPerkara(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_np,
            Response.Listener { response ->
                daftarNp.clear()
                if (response.equals(0)) {
                    Toast.makeText(this.context,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("id_np",jsonObject.getString("id_np"))
                        frm.put("id_bukti",jsonObject.getString("id_bukti"))
                        frm.put("nama_np",jsonObject.getString("nama_np"))
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("status_np",jsonObject.getString("status_np"))

                        daftarNp.add(frm)
                    }
                    npAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this.context,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "readAllNp" -> {
                        hm.put("mode","readAllNpNon")
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this.context)
        queue.add(request)
    }
}