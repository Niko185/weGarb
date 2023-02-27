package com.example.wegarb.view.activitys

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import com.example.wegarb.R
import com.example.wegarb.databinding.ActivityMainBinding
import com.example.wegarb.utils.FragmentManager
import com.example.wegarb.view.fragments.AccountFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickBottomNavigationMenu()
        launcherFragment()
    }

    private fun onClickBottomNavigationMenu() = with(binding)  {
        bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId) {
              R.id.icon_account -> FragmentManager.setFragment(AccountFragment.newInstance(), this@MainActivity)
                //R.id.icon_settings->

            }
            true
        }
    }

    private fun launcherFragment() = with(binding) {
        FragmentManager.setFragment(AccountFragment.newInstance(), this@MainActivity)
        bottomNavigationMenu.selectedItemId = R.id.icon_account

    }














}