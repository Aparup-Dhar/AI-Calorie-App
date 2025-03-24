package com.example.calorie_app_prototype

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.calorie_app_prototype.api.RetrofitInstance
import com.example.calorie_app_prototype.api.NutritionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PredictionActivity : AppCompatActivity() {
    private lateinit var caloriePredictionModel: CaloriePredictionModel
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prediction)

        caloriePredictionModel = CaloriePredictionModel(assets, "model.tflite", "labels.txt")
        progressBar = findViewById(R.id.progressBar)

        // Start loading animation
        progressBar.visibility = View.VISIBLE

        val imageUri = intent.getStringExtra("imageUri")
        imageUri?.let {
            val bitmap = uriToBitmap(Uri.parse(it))
            bitmap?.let {
                val (resizedBitmap, predictedLabel) = caloriePredictionModel.predict(it)
                findViewById<ImageView>(R.id.imageView).setImageBitmap(resizedBitmap)
                findViewById<TextView>(R.id.predictionTextView).text = "Prediction: $predictedLabel"

                // Fetch calorie information and update loading state
                fetchCalories(predictedLabel)
            }
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun fetchCalories(foodName: String) {
        val apiService = RetrofitInstance.api
        apiService.getNutrition(foodName).enqueue(object : Callback<NutritionResponse> {
            override fun onResponse(call: Call<NutritionResponse>, response: Response<NutritionResponse>) {
                if (response.isSuccessful) {
                    val calories = response.body()?.items?.firstOrNull()?.calories
                    findViewById<TextView>(R.id.caloriesTextView).text = "Calories: ${calories ?: "N/A"}"
                } else {
                    findViewById<TextView>(R.id.caloriesTextView).text = "Calories: N/A"
                }
                // Hide progress bar once data is loaded
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<NutritionResponse>, t: Throwable) {
                findViewById<TextView>(R.id.caloriesTextView).text = "Error fetching calories"
                t.printStackTrace()
                // Hide progress bar if there's an error
                progressBar.visibility = View.GONE
            }
        })
    }
}
