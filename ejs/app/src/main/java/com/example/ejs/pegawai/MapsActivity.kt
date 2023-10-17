package com.example.ejs.pegawai

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ejs.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import mumayank.com.airlocationlibrary.AirLocation
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {
    var airLoc : AirLocation? = null
    var gMap : GoogleMap? = null
    lateinit var mapFragment : SupportMapFragment

    val  RC_HASIL_SUKSES : Int=100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        tvKota.setText("Kab. Kediri")

        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fab.setOnClickListener(this)
        btnNext.setOnClickListener(this)
    }

    //method onActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airLoc?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
    //method onRequestPermissionsResult
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        airLoc?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        if (gMap!=null) {
            airLoc = AirLocation(this, true, true,
                object : AirLocation.Callbacks {
                    override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                        Toast.makeText(
                            this@MapsActivity, "Gagal mendapatkan posisi saat ini",
                            Toast.LENGTH_SHORT
                        ).show()
                        edtMaps.setText("Gagal mendapatkan posisi saat ini")
                    }

                    override fun onSuccess(location: Location) {
                        val ll = LatLng(location.latitude, location.longitude)
                        gMap!!.addMarker(MarkerOptions().position(ll).title("Posisi saya"))
                        gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 16.0f))
                        edtMaps.setText("${location.latitude}, " + "${location.longitude}")
                        tvJln.setText(getJlnName(location.latitude,location.longitude))
                        tvDesa.setText(getDesaName(location.latitude,location.longitude))
                        tvKec.setText(getKecName(location.latitude,location.longitude))
                        tvNegara.setText(getCountryName(location.latitude,location.longitude))
                    }
                })
        }
    }

    private fun getDesaName(lat: Double, long: Double): String {
        var desaName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addressList = geoCoder.getFromLocation(lat, long, 1)

        if (addressList != null && addressList.isNotEmpty()) {
            val address = addressList[0]
            desaName = address.subLocality ?: ""
        }

        return desaName
    }

    private fun getKecName(lat: Double, long: Double): String {
        var kecName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addressList = geoCoder.getFromLocation(lat, long, 1)

        if (addressList != null && addressList.isNotEmpty()) {
            val address = addressList[0]
            kecName = address.locality ?: ""
        }

        return kecName
    }

    private fun getJlnName(lat: Double, long: Double): String {
        var jlnName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addressList = geoCoder.getFromLocation(lat, long, 1)

        if (addressList != null && addressList.isNotEmpty()) {
            val address = addressList[0]
            jlnName = address.thoroughfare ?: ""
        }

        return jlnName
    }

    private fun getCountryName(lat: Double, long: Double): String {
        var countryName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addressList = geoCoder.getFromLocation(lat, long, 1)

        if (addressList != null && addressList.isNotEmpty()) {
            val address = addressList[0]
            countryName = address.countryName ?: ""
        }

        return countryName
    }

    private fun getProvinces(lat: Double, long: Double): List<String> {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val addressList = geoCoder.getFromLocation(lat, long, 1)
        val provinces = mutableListOf<String>()
        if (addressList != null && addressList.isNotEmpty()) {
            val address = addressList[0]
            address.adminArea?.let { provinces.add(it) }
            address.subAdminArea?.let { provinces.add(it) }
            address.locality?.let { provinces.add(it) }
        }
        return provinces
    }

    override fun onClick(v: View?) {
        airLoc = AirLocation(this,true,true,
            object : AirLocation.Callbacks{
                override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                    Toast.makeText(this@MapsActivity, "Gagal mendapatkan posisi saat ini",
                        Toast.LENGTH_SHORT).show()
                    edtMaps.setText("Gagal mendapatkan posisi saat ini")
                }

                override fun onSuccess(location: Location) {
                    val ll = LatLng(location.latitude,location.longitude)
                    gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,16.0f))
                    edtMaps.setText("${location.latitude}, " + "${location.longitude}")
                    tvJln.setText(getJlnName(location.latitude,location.longitude))
                    tvDesa.setText(getDesaName(location.latitude,location.longitude))
                    tvKec.setText(getKecName(location.latitude,location.longitude))
                    tvNegara.setText(getCountryName(location.latitude,location.longitude))
                }
            })

        when(v?.id){
            R.id.btnNext -> {
                var intentNext = Intent(this, FormPegawaiActivity::class.java)
                intentNext.putExtra("maps",edtMaps.text.toString())
                intentNext.putExtra("jln",tvJln.text.toString())
                intentNext.putExtra("desa",tvDesa.text.toString())
                intentNext.putExtra("kec",tvKec.text.toString())
                intentNext.putExtra("kode", "sukses")
                startActivityForResult(intentNext,RC_HASIL_SUKSES)
                startActivity(intentNext)
            }
        }
    }
}