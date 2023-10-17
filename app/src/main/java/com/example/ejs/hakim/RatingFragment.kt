package com.example.ejs.hakim

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.BaseApplication
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.admin.BuktiDetailAdminActivity
import kotlinx.android.synthetic.main.fragment_rating.insCatatan
import kotlinx.android.synthetic.main.fragment_rating.insRatingBar
import kotlinx.android.synthetic.main.fragment_rating.view.btnSimpan
import org.json.JSONObject

class RatingFragment : DialogFragment() {
    lateinit var v: View
    lateinit var parent: BuktiDetailAdminActivity
    lateinit var urlClass: UrlClass

    lateinit var preferences: SharedPreferences
    val PREF_NAME = "akun"
    val NIP = "nip"
    val DEF_NIP = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parent = activity as BuktiDetailAdminActivity
        v = inflater.inflate(R.layout.fragment_rating, container, false)

        preferences = v.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        urlClass = UrlClass()

        v.btnSimpan.setOnClickListener {
            insert()
            BaseApplication.notificationHelper.showRatingNotification()
        }

        return v
    }

    private fun insert() {
        val request = object : StringRequest(
            Method.POST,urlClass.url_bukti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if (respon.equals("0")) {
                    Toast.makeText(v.context, "Anda telah memberi Rating!", Toast.LENGTH_SHORT).show()
                } else {
                    dismiss()
                    parent.buktiDetail("detailBukti")
                    parent.rating("rating")
                    parent.showKomen()
                    Toast.makeText(v.context, "Berhasil memberi rating!", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(v.context,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                val rating = insRatingBar.rating.toDouble()
                hm.put("mode", "insert_rating")
                hm.put("id_bukti", arguments?.get("id").toString())
                hm.put("rating", rating.toString())
                hm.put("catatan", insCatatan.text.toString())
                hm.put("nip", preferences.getString(NIP,DEF_NIP).toString())

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(v.context)
        queue.add(request)
    }
}