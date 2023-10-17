package com.example.ejs.hakim

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ejs.R
import com.example.ejs.UrlClass
import com.example.ejs.adapter.AdapterLaporanStatus
import kotlinx.android.synthetic.main.fragment_laporan_status.view.rvLaporan
import kotlinx.android.synthetic.main.fragment_laporan_status.view.spSortingBulan
import kotlinx.android.synthetic.main.fragment_laporan_status.view.spSortingTahun
import kotlinx.android.synthetic.main.fragment_laporan_status.view.textNotFound
import org.json.JSONArray

class FragmentLaporanTelat : Fragment() {
    private lateinit var v: View
    lateinit var urlClass: UrlClass

    val daftarBukti = mutableListOf<HashMap<String,String>>()
    lateinit var buktiAdapter: AdapterLaporanStatus
    lateinit var parent : LaporanDetailActivity

    val sortSp = arrayOf("--Filter--",
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember")
    lateinit var adapterSort: ArrayAdapter<String>
    var nilaiSort = ""

    val tahunSp = arrayOf("2023","2022","2021","2020","2019","2018")
    lateinit var adapterTahun: ArrayAdapter<String>
    var nilaiTahun = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_laporan_status, container, false)
        parent = activity as LaporanDetailActivity

        urlClass = UrlClass()
        buktiAdapter = AdapterLaporanStatus(daftarBukti)
        v.rvLaporan.layoutManager = LinearLayoutManager(v.context)
        v.rvLaporan.adapter = buktiAdapter

        adapterSort = ArrayAdapter(v.context, android.R.layout.simple_list_item_1,sortSp)
        v.spSortingBulan.adapter = adapterSort
        v.spSortingBulan.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    showDataBukti("", "")
                } else {
                    v.spSortingTahun.visibility = View.VISIBLE
                    nilaiSort = position.toString()
                    showDataBukti(nilaiSort, "")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        adapterTahun = ArrayAdapter(v.context, android.R.layout.simple_list_item_1, tahunSp)
        v.spSortingTahun.adapter = adapterTahun
        v.spSortingTahun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position)

                showDataBukti(nilaiSort, selectedItem.toString())
                nilaiTahun = selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        return v
    }

    override fun onStart() {
        super.onStart()
        showDataBukti("","")
    }

    private fun showDataBukti(bln: String, thn: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.url_laporan,
            Response.Listener { response ->
                daftarBukti.clear()
                val jsonArray = JSONArray(response)
                if (jsonArray.length() == 0) {
                    v.textNotFound.visibility = View.VISIBLE
                    v.rvLaporan.visibility = View.GONE
                } else {
                    v.textNotFound.visibility = View.GONE
                    v.rvLaporan.visibility = View.VISIBLE

                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  bkt = java.util.HashMap<String, String>()
                        bkt.put("id_bukti",jsonObject.getString("id_bukti"))
                        bkt.put("jamtanggal",jsonObject.getString("jamtanggal"))
                        bkt.put("nama",jsonObject.getString("nama"))
                        bkt.put("nomor_perkara",jsonObject.getString("nomor_perkara"))
                        bkt.put("jabatan",jsonObject.getString("jabatan"))
                        bkt.put("url",jsonObject.getString("url"))

                        daftarBukti.add(bkt)
                    }
                    buktiAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                hm.put("mode", "laporan_telat")
                hm.put("nip", parent.nip)
                hm.put("bulan", bln)
                hm.put("tahun", thn)

                return hm
            }
        }
        val queue = Volley.newRequestQueue(v.context)
        queue.add(request)
    }
}