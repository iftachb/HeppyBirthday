package com.example.happybirthday

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

class MainViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val birthday = MutableLiveData<Date>()

    val pickImageLiveData = MutableLiveData<String>()
    val pictureLiveData = MutableLiveData<Pair<Bitmap, Uri>>()

    var photoFile1: File? = null
    var photoFile2: File? = null
}