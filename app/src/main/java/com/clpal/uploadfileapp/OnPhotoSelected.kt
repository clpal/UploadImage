package com.clpal.uploadfileapp
import android.net.Uri

interface OnPhotoSelected {
    fun selectedPhoto(currency: Uri)
}