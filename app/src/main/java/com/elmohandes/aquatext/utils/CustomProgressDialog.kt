package com.elmohandes.aquatext.utils

import android.app.Activity
import android.app.AlertDialog
import com.elmohandes.aquatext.R

class CustomProgressDialog(private val activity: Activity) {
    private lateinit var alertDialog: AlertDialog

    fun startLoading(){
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = activity.layoutInflater
        builder.setView(layoutInflater.inflate(R.layout.custom_progress_dialog,null))
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun dismissDialog(){
        alertDialog.dismiss()
    }

}