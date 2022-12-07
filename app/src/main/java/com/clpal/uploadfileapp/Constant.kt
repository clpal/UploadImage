package com.clpal.uploadfileapp

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class Constant {
    companion object{
        fun showAlert(context: Context,message: String) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage(message)
            builder.setPositiveButton(R.string.ok_button_title, null)

            val dialog = builder.create()
            dialog.show()
        }
         fun Context.showMsg(msg: String){
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
        }
        fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}