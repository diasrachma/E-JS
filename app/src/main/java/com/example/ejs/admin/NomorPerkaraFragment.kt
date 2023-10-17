package com.example.ejs.admin

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_nomor_perkara.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class NomorPerkaraFragment : DialogFragment() {

    lateinit var thisParent : NomorPerkaraActivity
    lateinit var urlClass: UrlClass
    lateinit var v : View

    var mildetik = 0
    var detik = 0

    lateinit var namaAdapter: ArrayAdapter<String>
    val daftarNama = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as NomorPerkaraActivity
        urlClass = UrlClass()
        v = inflater.inflate(R.layout.fragment_nomor_perkara, container, false)

        val cal : Calendar = Calendar.getInstance()

        namaAdapter = ArrayAdapter(thisParent, android.R.layout.simple_list_item_1,daftarNama)
        v.txNamaPegawaiNp.setAdapter(namaAdapter)
        v.txNamaPegawaiNp.threshold = 0
        v.txNamaPegawaiNp.setOnItemClickListener { parent, view, position, id ->
            showNip("showProfilByNama")
        }

        mildetik = cal.get(Calendar.MILLISECOND)
        detik = cal.get(Calendar.SECOND)

        v.txIdNomorPerkara.setText("NP-0$detik$mildetik")

        v.btnKirimNomorPerkara.setOnClickListener {
            AlertDialog.Builder(thisParent)
                .setTitle("Tambah Nomor Perkara!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah anda ingin menambah Nomor Perkara?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    insertNomorPerkara("insert")
                    thisParent.restartActivity()
                    dismiss()
                    Toast.makeText(thisParent, "Nomor Perkara telah ditambahkan", Toast.LENGTH_LONG).show()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    Toast.makeText(thisParent, "Membatalkan tambah Nomor Perkara", Toast.LENGTH_LONG).show()
                })
                .show()
        }

    v.btnBatalKirimNomorPerkara.setOnClickListener {
            dismiss()
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        getNama("getNama")
    }

    fun showNip(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_user,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nip = jsonObject.getString("nip")

                v.txNipNp.setText(nip)
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode) {
                    "showProfilByNama" -> {
                        hm.put("mode", "showProfilByNama")
                        hm.put("nama", v.txNamaPegawaiNp.text.toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    private fun getNama(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_user,
            Response.Listener { response ->
                daftarNama.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarNama.add(jsonObject.getString("nama"))
                }
                namaAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->

            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode) {
                    "getNama" -> {
                        hm.put("mode", "getNama")
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }

    private fun insertNomorPerkara(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_np,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                hm.put("id_np",v.txIdNomorPerkara.text.toString())
                hm.put("nama_np",v.txNamaNp.text.toString())
                hm.put("nip",v.txNipNp.text.toString())

                when(mode) {
                    "insert" -> {
                        hm.put("mode", "insert")
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
}