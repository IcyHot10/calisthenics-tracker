package com.indeavour.caltracker

import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.indeavour.caltracker.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var database: AppDatabase
    lateinit var pager : ViewPager2
    lateinit var tab: TabLayout
    lateinit var bar: androidx.appcompat.widget.Toolbar
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        pager = findViewById(R.id.viewPager2)
        tab = findViewById(R.id.tabs)
        bar = findViewById(R.id.toolbar)
        setSupportActionBar(bar)
        val adapter = ViewPagerAdapter(this)

        adapter.addFragment(DailyLayout(), "Daily exercise")
        adapter.addFragment(StatsLayout(), "Stats")
        adapter.addFragment(FullLayout(), "Exercise Overview")

        pager.adapter = adapter

        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val fragment: Fragment? = getFragment(pager.adapter as ViewPagerAdapter, position) // Ensure you get the right fragment
                if (fragment is StatsLayout) {
                    fragment.onFragmentResume()
                }
                if (fragment is DailyLayout) {
                    fragment.onFragmentResume()
                }
            }
        })

        TabLayoutMediator(tab, pager) {t, p ->
            t.text = when (p) {
                0 -> "Daily exercise"
                1 -> "Stats"
                2 -> "Exercise Overview"
                else -> ""
            }
        }.attach()
        lifecycleScope.launch(Dispatchers.IO){
            database = AppDatabase.getDatabase(this@MainActivity)
        }
    }

    fun getFragment(adapter: ViewPagerAdapter, position: Int): Fragment {
        return adapter.createFragment(position)
    }
}