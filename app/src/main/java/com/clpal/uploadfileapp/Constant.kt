package com.clpal.uploadfileapp

import android.app.Activity
import android.content.Context
import android.widget.Button
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


        private fun showErrorAlert(context: Activity){
            val builder = AlertDialog.Builder(context,R.style.CustomAlertDialog)
                .create()
            val view =context.layoutInflater.inflate(R.layout.fragment_custom_error_dialog,null)
            val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
            builder.setView(view)
            button.setOnClickListener {
                builder.dismiss()
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()
        }
        private fun showPictureDialog(context: Activity) {
            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems
            ) { dialog, which ->
                when (which) {
                    //0 -> choosePhotoFromGallary()
                    // 1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
    }

}