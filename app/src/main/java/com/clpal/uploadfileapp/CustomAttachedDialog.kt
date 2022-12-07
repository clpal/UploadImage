package com.clpal.uploadfileapp

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.clpal.uploadfileapp.Constant.Companion.toast
import com.clpal.uploadfileapp.databinding.FragmentCustomDialogBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

// https://demonuts.com/pick-image-gallery-camera-android-kotlin/
class MyCustomDialog : DialogFragment(),View.OnClickListener {
    //   private var label: TextView? = null
    private var _binding: FragmentCustomDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val REQUEST_WRITE_PERMISSION = 100
       const val REQUEST_CODE_GALLERY = 200
        private const val REQUEST_CODE_CAMERA = 2
        private val IMAGE_DIRECTORY = "/demonuts"
    }
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireActivity())
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.window?.setGravity(Gravity.BOTTOM)
        _binding = FragmentCustomDialogBinding.inflate(inflater, container, false)
        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.takePhoto.setOnClickListener {
            if (checkPermissionForReadExtertalStorage()) {
                takePhotoFromCamera()

            } else {
                // Make Permission Request
                requestPermission()
            }
        }
        binding.chooseExisting.setOnClickListener {
            if (checkPermissionForReadExtertalStorage()) {
                // Permission Already Granted
          /*      val label: String = getString(R.string.permission_granted)
                activity?.toast(label)*/
                openGalleryForImages()
            } else {
                // Make Permission Request
                requestPermission()
            }
        }
        binding.allowPermission.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    private fun checkPermissionForReadExtertalStorage(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        } else false
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA), REQUEST_WRITE_PERMISSION)
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){

            REQUEST_WRITE_PERMISSION->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (requestCode == REQUEST_CODE_GALLERY){
                        //permission from popup granted
                        binding.allowPermission!!.visibility = View.GONE
                        openGalleryForImages()
                    }
                   else if (requestCode == REQUEST_CODE_CAMERA){
                        takePhotoFromCamera()
                    }
                }
                else {
                    //permission from popup denied
                    val label: String = getString(R.string.permission_denied)
                    activity?.toast(label)
                    // Check wheather checked dont ask again
                    checkUserRequestedDontAskAgain()
                }
            }
        }

    }
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    private fun openGalleryForImages() {

        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Choose Pictures"), REQUEST_CODE_GALLERY)
        }
        else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_GALLERY){
            // if multiple images are selected
            if (data?.getClipData() != null) {
                var count = data.clipData?.itemCount

                for (i in 0..count!! - 1) {
                    var imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                     //   iv_image.setImageURI(imageUri) Here you can assign your Image URI to the ImageViews
                }
            } else if (data?.getData() != null) {
                // if single image is selected

                var imageUri: Uri = data.data!!
                //   iv_image.setImageURI(imageUri) Here you can assign the picked image uri to your imageview

            }
        }
        else if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            //imageview!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
            Toast.makeText(requireActivity(), "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }
    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File((Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        Log.d("fee",wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }
        try
        {
            Log.d("heel",wallpaperDirectory.toString())
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(requireActivity(),
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }
    private fun checkUserRequestedDontAskAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val rationalFalgREAD = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val rationalFalgWRITE = shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            val rationalFalgCamera = shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)
            if (!rationalFalgREAD && !rationalFalgWRITE && !rationalFalgCamera) {
                // Permission was denied
                val label: String = getString(R.string.permission_denied_forcefully)
                activity?.toast(label)
                binding.allowPermission!!.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v.let {  id }){

        }
    }
}


