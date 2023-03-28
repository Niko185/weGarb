package com.example.wegarb.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.wegarb.data.requests.search.SearchRequest
import com.example.wegarb.presentation.view.fragments.AccountFragment
import java.text.SimpleDateFormat
import java.util.*

fun Fragment.isPermissionGranted(namePermission: String): Boolean {
    return ContextCompat.checkSelfPermission(activity as AppCompatActivity, namePermission) == PackageManager.PERMISSION_GRANTED
}

fun AccountFragment.formatterUnix(unixTime: String): String {
    val unixSeconds = unixTime.toLong()
    val date = Date(unixSeconds * 1000)
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val formattedDate = sdf.format(date)
    return formattedDate.toString()
}

fun SearchRequest.formatterUnix(unixTime: String): String {
    val unixSeconds = unixTime.toLong()
    val date = Date(unixSeconds * 1000)
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    val formattedDate = sdf.format(date)
    return formattedDate.toString()
}