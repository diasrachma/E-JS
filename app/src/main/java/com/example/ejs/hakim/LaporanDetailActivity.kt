package com.example.ejs.hakim

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.ejs.R
import com.google.android.material.tabs.TabLayout

class LaporanDetailActivity : AppCompatActivity() {
    var nip = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporan_detail)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = PageLaporan(supportFragmentManager)

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)

        nip = intent?.getStringExtra("nip").toString()
    }

    class PageLaporan (fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            when(position) {
                0 -> {
                    return FragmentLaporanTerkirim()
                }
                1 -> {
                    return FragmentLaporanTelat()
                }
                else -> {
                    return FragmentLaporanTerkirim()
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position) {
                0 -> {
                    return "Terkirim"
                }
                1 -> {
                    return "Telat"
                }
            }
            return super.getPageTitle(position)
        }
    }
}