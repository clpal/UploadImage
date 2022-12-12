package com.clpal.uploadfileapp

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.clpal.uploadfileapp.databinding.FragmentFirstBinding
import java.io.File

import android.os.Handler
import android.text.method.LinkMovementMethod
import android.view.Gravity

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() ,OnPhotoSelected{
    var isStarted = false
    var progressStatus = 0
    var handler: Handler? = null
    var secondaryHandler: Handler? = Handler()
    var primaryProgressStatus = 0
    var secondaryProgressStatus = 0


    private var progressBarStatus = 0
    var dummy:Int = 0
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)




        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewAttached.setOnClickListener {
           MyCustomDialog(this).show(requireActivity().supportFragmentManager, "MyCustomFragment")
            //showAlertBox()
        }
        binding.cancel.setOnClickListener {

            binding.rl.visibility = View.GONE
        }

binding.textViewLink.setOnClickListener {
    showDownloadAlert()
}


    }
private fun onProgressBar(){

    // task is run on a thread
    Thread(Runnable {
        // dummy thread mimicking some operation whose progress can be tracked
        while (progressBarStatus < 100) {
            // performing some dummy operation
            try {
                //  dummy = dummy+1
                dummy += 2
                //primaryProgressStatus += 1
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            // tracking progress
            progressBarStatus = dummy

            // Updating the progress bar
            binding.progressBarHorizontal.progress = progressBarStatus
            ///binding.textViewLink.movementMethod = LinkMovementMethod.getInstance()
        }

    }).start()
}
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun selectedPhoto(uri: Uri) {
     //   binding.rl.visibility = View.VISIBLE
        if (getFileSize(uri)/1024>1096) {
            showErrorAlert()
        }

        else{
            onProgressBar()
            //val fileName = File(uri.path).name
            val fileName = File(uri.path)
// or
            // val fileName = uri.pathSegments.last()
            // binding.tvAttachment.text = uri.lastPathSegment
            var filesize = fileName.length()/1024
            binding.fileName.text = uri.getName(requireContext())+"-"+getFileSize(uri)/1024+"KB"
            binding.rl.visibility = View.VISIBLE
        }
    }
    private fun Uri.getName(context: Context): String? {
        val returnCursor = context.contentResolver.query(this, null, null, null, null)
        val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor?.moveToFirst()
        val fileName = nameIndex?.let { returnCursor.getString(it) }
        returnCursor?.close()
        return fileName
        // https://stackoverflow.com/questions/5568874/how-to-extract-the-file-name-from-uri-returned-from-intent-action-get-content
    }
    private fun getFileSize(uri: Uri): Long {
        // https://stackoverflow.com/questions/24864257/getting-size-of-an-imagein-kb-or-mb-selected-from-gallery-programatically
        val cursor: Cursor? = context?.contentResolver!!.query(uri, null, null, null, null)

        cursor?.use { val sizeColumn = it.getColumnIndexOrThrow(android.provider.MediaStore.MediaColumns.SIZE)
            if (it.moveToNext()) {
                return it.getLong(sizeColumn)
            }
        }
        return 0L
    }
    fun getFileFromUri(uri: Uri): File? {
        if (uri.path == null) {
            return null
        }
        var realPath = String()
        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (uri.path!!.contains("/document/image:")) {
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else {
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val column = "_data"
            val projection = arrayOf(column)
            val cursor = context?.contentResolver?.query(
                databaseUri,
                projection,
                selection,
                selectionArgs,
                null
            )
            cursor?.let {
                if (it.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(column)
                    realPath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Log.i("GetFileUri Exception:", e.message ?: "")
        }
        val path = if (realPath.isNotEmpty()) realPath else {
            when {
                uri.path!!.contains("/document/raw:") -> uri.path!!.replace(
                    "/document/raw:",
                    ""
                )
                uri.path!!.contains("/document/primary:") -> uri.path!!.replace(
                    "/document/primary:",
                    "/storage/emulated/0/"
                )
                else -> return null
            }
        }
        return File(path)}



    private fun showErrorAlert(){
        val builder = AlertDialog.Builder(requireActivity(),R.style.CustomAlertDialog)
            .create()
        val view = layoutInflater.inflate(R.layout.fragment_custom_error_dialog,null)
        val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
        builder.setView(view)
        button.setOnClickListener {
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

        private fun showDownloadAlert(){
            val builder = AlertDialog.Builder(requireActivity(),R.style.CustomAlertDialog)
                .create()
            val view = layoutInflater.inflate(R.layout.download_dialog,null)
            val  button = view.findViewById<Button>(R.id.dialogDismiss_button)
            builder!!.window?.setGravity(Gravity.BOTTOM)
            builder.setView(view)
            button.setOnClickListener {
                builder.dismiss()
            }
            builder.setCanceledOnTouchOutside(false)
            builder.show()
        }
}