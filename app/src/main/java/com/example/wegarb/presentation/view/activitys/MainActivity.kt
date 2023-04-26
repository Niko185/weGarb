package com.example.wegarb.presentation.view.activitys

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.wegarb.R
import com.example.wegarb.databinding.ActivityMainBinding
import com.example.wegarb.presentation.view.fragments.AccountFragment
import com.example.wegarb.presentation.view.fragments.DaysFragment
import com.example.wegarb.utils.FragmentManager
import com.example.wegarb.utils.SearchDialog
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val accountFragment = AccountFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentHolder, accountFragment, "AccountFragment")
                .commit()
        }
        onClickBottomNavigationMenu()
        launcherFragment()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_action_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.icon_my_location) {
            val accountFragmentInstance = supportFragmentManager.findFragmentById(R.id.fragmentHolder) as AccountFragment
            if (accountFragmentInstance.isAdded) {
                accountFragmentInstance.getMyLocationNow()
            }

        } else if (item.itemId == R.id.icon_search_city) {
            SearchDialog.searchCityDialog(this, object : SearchDialog.Listener {
                override fun searchCity(cityName: String?) {
                    val accountFragmentInstance = supportFragmentManager.findFragmentById(R.id.fragmentHolder) as AccountFragment
                    if (accountFragmentInstance.isAdded) {
                        cityName?.let { accountFragmentInstance.requestForSearch(it) }
                        accountFragmentInstance.showDataHeadCardOnScreenObserverSearch()
                    }
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onClickBottomNavigationMenu() = with(binding)  {
        bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.icon_account -> FragmentManager.setFragment(AccountFragment.newInstance(), this@MainActivity)
                R.id.icon_favorite -> FragmentManager.setFragment(DaysFragment.newInstance(), this@MainActivity)
            }
            true
        }
    }


    private fun launcherFragment() = with(binding) {
        FragmentManager.setFragment(AccountFragment.newInstance(), this@MainActivity)
        bottomNavigationMenu.selectedItemId = R.id.icon_account
    }
}