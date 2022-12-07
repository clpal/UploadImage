package com.clpal.uploadfileapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.clpal.uploadfileapp.databinding.FragmentCustomDialogBinding

class MyCustomDialog: DialogFragment() {
 //   private var label: TextView? = null

    private var _binding: FragmentCustomDialogBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    companion object {
        const val REQUEST_WRITE_PERMISSION = 100
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        dialog!!.setCanceledOnTouchOutside(false)

//set gravity bottom
        dialog!!.window?.setGravity(Gravity.BOTTOM)

        _binding = FragmentCustomDialogBinding.inflate(inflater, container, false)


        binding.cancel.setOnClickListener {
            dismiss()
        }
        binding.takePhoto.setOnClickListener {

        }
        binding.chooseExisting.setOnClickListener {
            if (checkPermissionForReadExtertalStorage()) {
                // Permission Already Granted
                binding.label?.setText(R.string.permission_granted)
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
                    || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        } else false
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0) {
            if (requestCode == REQUEST_WRITE_PERMISSION)
                if (grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    binding.label?.setText(R.string.permission_granted)
                    binding.allowPermission!!.visibility = View.GONE
                } else {
                    // permission denied
                    binding.label?.setText(R.string.permission_denied)
                    // Check wheather checked dont ask again
                    checkUserRequestedDontAskAgain()
                }
        }
    }
    private fun checkUserRequestedDontAskAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val rationalFalgREAD =
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val rationalFalgWRITE =
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (!rationalFalgREAD && !rationalFalgWRITE) {
                binding.label!!.setText(R.string.permission_denied_forcefully)
                // Permission was denied
              // Constant. showAlert(requireActivity(),R.string.permission_denied_forcefully)

                binding.allowPermission!!.visibility = View.VISIBLE
            }
        }}
    override fun onResume() {
        super.onResume()
        if (checkPermissionForReadExtertalStorage()) {
            binding.label!!.setText(R.string.permission_granted)
            binding.allowPermission.visibility=View.GONE
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(activity as Context)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok_button_title, null)

        val dialog = builder.create()
        dialog.show()
    }

}