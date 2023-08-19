package com.example.wegarb.presentation.view.activitys


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wegarb.R
import com.example.wegarb.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationMenu)
        bottomNav.setupWithNavController(navController)
    }
}




