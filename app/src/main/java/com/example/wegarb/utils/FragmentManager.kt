package com.example.wegarb.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wegarb.R

object FragmentManager: Fragment() {

    var currentFragment: Fragment? = null

    fun setFragment(fragment: Fragment, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentHolder, fragment)
        transaction.commit()
        currentFragment = fragment
    }



}