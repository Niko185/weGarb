package com.example.wegarb.view.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.icon_my_location) {
            // code
            } else if(item.itemId == R.id.icon_edit_location) {
                //code
            }
        return super.onOptionsItemSelected(item)
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