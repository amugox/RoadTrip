package co.ke.jamboapps.roadtrip

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.FrameLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.ke.jamboapps.roadtrip.fragment.HomeFragment
import co.ke.jamboapps.roadtrip.service.MyLocationService
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity() {

    private lateinit var navButton: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navButton = findViewById(R.id.bottomNav)

        if (savedInstanceState == null) {
            if (findViewById<FrameLayout>(R.id.fragment) != null) {
                val fragment: Fragment = HomeFragment()
                supportFragmentManager.beginTransaction().add(R.id.fragment, fragment).commit()
            }
        }

        navButton.setOnItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> {
                    fragment = HomeFragment()
                }
                R.id.nav_trips -> {
                    fragment = HomeFragment()
                }
                R.id.nav_settings -> {
                    fragment = HomeFragment()
                }
            }

            if (fragment != null) {
                changeFragment(fragment, false)
                true
            } else
                false

        }
    }

}