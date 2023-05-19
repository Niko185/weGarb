package com.example.wegarb.presentation.view.activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wegarb.R
import com.example.wegarb.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClickBottomNavigationMenu()
    }

    private fun onClickBottomNavigationMenu() = with(binding)  {
        bottomNavigationMenu.setOnItemSelectedListener {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController

            when(it.itemId) {
                R.id.icon_account -> navController.navigate(R.id.weatherFragment)
                R.id.icon_favorite -> navController.navigate(R.id.historyFragment)
            }
            true
        }
    }
}