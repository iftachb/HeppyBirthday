package com.example.happybirthday

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object {
        private val RESULT_PHOTO = 1
        private val RESULT_GALLERY = 2
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment? ?: return

        val navController = host.navController

        appBarConfiguration = AppBarConfiguration(setOf(R.id.fragFirst))
        setupActionBarWithNavController(navController, appBarConfiguration)

        viewModel.pickImageLiveData.observe(this) {fragmentFirst ->
            selectImage(fragmentFirst)
        }

    }

    private fun selectImage(className : String) {
        checkPermission(9)
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        println(this.getExternalFilesDir(null))
        if (className == DetailsFragment::class.java.name)
            viewModel.photoFile1 = File.createTempFile("photo", ".jpg" , this.getExternalFilesDir(null))
        else
            viewModel.photoFile2 = File.createTempFile("photo", ".jpg" , this.getExternalFilesDir(null))
        builder.setItems(options, DialogInterface.OnClickListener { dialog, item ->
            if (options[item] == "Take Photo") {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                    // Ensure that there's a camera activity to handle the intent
                    this.also { context ->
                        intent.resolveActivity(context.packageManager)?.also {
                            var file = if (className == DetailsFragment::class.java.name)
                                viewModel.photoFile1
                            else
                                viewModel.photoFile2
                            file.also {
                                val photoURI: Uri = FileProvider.getUriForFile(
                                    context,
                                    "com.example.happybirthday.fileprovider",
                                    it!!
                                )
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                                startActivityForResult(intent, RESULT_PHOTO)
                            }
                        }
                    }
                }
            } else if (options[item] == "Choose from Gallery") {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, RESULT_GALLERY)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        })
        builder.show()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = Uri.fromFile(if (viewModel.pickImageLiveData.value == DetailsFragment::class.java.name)
                    viewModel.photoFile1
                else
                    viewModel.photoFile2)
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                viewModel.pictureLiveData.postValue(bitmap to uri)
            }
        } else if (requestCode == RESULT_GALLERY) {
            val selectedImage: Uri? = data!!.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val c: Cursor = this.applicationContext.contentResolver.query(selectedImage!!, filePath, null, null, null)!!
            c.moveToFirst()
            val columnIndex: Int = c.getColumnIndex(filePath[0])
            val picturePath: String = c.getString(columnIndex)
            c.close()
            val bitmap = BitmapFactory.decodeFile(picturePath)
            viewModel.pictureLiveData.postValue(bitmap to selectedImage)
        }
    }

    private fun checkPermission(requestCode: Int) {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }
}