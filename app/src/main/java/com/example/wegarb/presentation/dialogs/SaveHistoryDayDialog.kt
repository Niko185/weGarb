package com.example.wegarb.presentation.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.wegarb.databinding.DialogSaveBinding

object SaveHistoryDayDialog {

    fun start(context: Context, listener: Listener){
        val builder = AlertDialog.Builder(context)
        val binding = DialogSaveBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.bPositiveOkey.setOnClickListener {
            listener.onClickComfort()
            dialog.dismiss()
        }

        binding.bNegativeSaveCold.setOnClickListener {
            listener.onClickCold()
            dialog.dismiss()
        }

        binding.bNegativeSaveHot.setOnClickListener {
            listener.onClickHot()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    interface Listener {
        fun onClickComfort()
        fun onClickCold()
        fun onClickHot()
    }
}