package com.example.wegarb.presentation.view.activitys

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.wegarb.AppDatabaseInstance
import com.example.wegarb.R
import com.example.wegarb.databinding.ActivityMainBinding
import com.example.wegarb.presentation.view.fragments.history.HistoryFragment
import com.example.wegarb.presentation.view.fragments.weather.WeatherFragment
import com.example.wegarb.project_utils.FragmentManager
import com.example.wegarb.presentation.utils.SearchDialog
import com.example.wegarb.presentation.view.fragments.weather.WeatherViewModel
import androidx.activity.viewModels



class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels {
        WeatherViewModel.WeatherViewModelFactory((applicationContext as AppDatabaseInstance).database)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            val weatherFragment = WeatherFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentHolder, weatherFragment, "AccountFragment")
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
            val weatherFragmentInstance = supportFragmentManager.findFragmentById(R.id.fragmentHolder) as WeatherFragment
            if (weatherFragmentInstance.isAdded) {
                weatherFragmentInstance.getMyLocationNow()
            }

        } else if (item.itemId == R.id.icon_search_city) {
            SearchDialog.searchCityDialog(this, object : SearchDialog.Listener {
                override fun searchCity(cityName: String?) {
                    val weatherFragmentInstance = supportFragmentManager.findFragmentById(R.id.fragmentHolder) as WeatherFragment
                    if (weatherFragmentInstance.isAdded) {
                       cityName?.let { weatherViewModel.getSearchWeather(it) }
                    }
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onClickBottomNavigationMenu() = with(binding)  {
        bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.icon_account -> FragmentManager.setFragment(WeatherFragment.newInstance(), this@MainActivity)
                R.id.icon_favorite -> FragmentManager.setFragment(HistoryFragment.newInstance(), this@MainActivity)
            }
            true
        }
    }


    private fun launcherFragment() = with(binding) {
        FragmentManager.setFragment(WeatherFragment.newInstance(), this@MainActivity)
        bottomNavigationMenu.selectedItemId = R.id.icon_account
    }
}