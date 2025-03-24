package com.example.calorie_app_prototype

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class AddActivity : AppCompatActivity() {

    private lateinit var imageUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)


        // Gallery selection button
        val select_image_button = findViewById<CardView>(R.id.select_image_button)
        select_image_button.setOnClickListener {
            selectImageFromGallery.launch("image/*")
        }

        // Camera button
        val open_camera_button = findViewById<CardView>(R.id.open_camera_button)
        open_camera_button.setOnClickListener {
            // Request camera permission if not granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                openCamera()
            }
        }
    }

    private val selectImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                // Start PredictionActivity with the selected image
                val intent = Intent(this, PredictionActivity::class.java).apply {
                    putExtra("imageUri", it.toString())
                }
                startActivity(intent)
            }
        }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                // Start PredictionActivity with the captured image
                val intent = Intent(this, PredictionActivity::class.java).apply {
                    putExtra("imageUri", imageUri.toString())
                }
                startActivity(intent)
            }
        }

    private fun openCamera() {
        // Create temporary file to store the photo
        val photoFile: File = try {
            createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
            return
        }
        imageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        takePicture.launch(imageUri)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir = cacheDir
        return File.createTempFile("captured_image", ".jpg", storageDir)
    }
}