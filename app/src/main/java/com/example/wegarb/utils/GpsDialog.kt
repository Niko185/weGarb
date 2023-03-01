package com.example.wegarb.utils

import android.app.AlertDialog
import android.content.Context

object GpsDialog {

    fun startDialog(context: Context, actionWithUser: ActionWithUser){
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()

        dialog.setTitle("Turn on GPS?")
        dialog.setMessage("GPS not found")
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK") { _, _ ->
            actionWithUser.transferUserGpsSettings()
            dialog.dismiss()
        }

        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL"){
                _,_ -> dialog.dismiss()
        }
        dialog.show()
    }

    interface ActionWithUser {
        fun transferUserGpsSettings()
    }
}