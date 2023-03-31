package com.example.wegarb.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


fun Fragment.isPermissionGranted(namePermission: String): Boolean {
    return ContextCompat.checkSelfPermission(activity as AppCompatActivity, namePermission) == PackageManager.PERMISSION_GRANTED
}
