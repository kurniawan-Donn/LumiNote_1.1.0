package com.example.LumiNote

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fab: FloatingActionButton
    private lateinit var mainHeader: TextView
    private lateinit var btnToProfil: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi views
        bottomNav = findViewById(R.id.bottom_nav_view)
        fab = findViewById(R.id.fab_add)
        mainHeader = findViewById(R.id.main_header)
        btnToProfil = findViewById(R.id.btnToProfil)

        setupNavigation()
        setupFab()
        setupBottomNavListener()
        setupBackPressHandler()
        setupProfileButton() // ✅ TAMBAHAN BARU
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.catatan_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNav.setupWithNavController(navController)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_catatan -> {
                    navController.navigate(R.id.navigation_catatan)
                    mainHeader.text = getString(R.string.daftar_catatan)
                    true
                }
                R.id.navigation_tugas -> {
                    navController.navigate(R.id.navigation_tugas)
                    mainHeader.text = getString(R.string.daftar_tugas)
                    true
                }
                R.id.navigation_plus -> {
                    showPilihTipeDialog()
                    false
                }
                else -> false
            }
        }
    }

    private fun setupFab() {
        fab.setOnClickListener {
            showPilihTipeDialog()
        }
    }

    private fun setupBottomNavListener() {
        bottomNav.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.navigation_catatan -> mainHeader.text = getString(R.string.daftar_catatan)
                R.id.navigation_tugas -> mainHeader.text = getString(R.string.daftar_tugas)
            }
        }
    }

    private fun showPilihTipeDialog() {
        val bottomSheet = BottomSheetDialog()
        bottomSheet.show(supportFragmentManager, "PilihTipeBottomSheet")
    }

    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.catatan_fragment) as NavHostFragment
                val navController = navHostFragment.navController

                if (navController.currentDestination?.id == R.id.navigation_catatan ||
                    navController.currentDestination?.id == R.id.navigation_tugas) {
                    finish()
                } else {
                    navController.navigateUp()
                }
            }
        })
    }

    // ✅ FUNGSI BARU: Setup tombol profil
    private fun setupProfileButton() {
        btnToProfil.setOnClickListener {
            val intent = Intent(this, ProfilActivity::class.java)
            startActivity(intent)
        }
    }
}