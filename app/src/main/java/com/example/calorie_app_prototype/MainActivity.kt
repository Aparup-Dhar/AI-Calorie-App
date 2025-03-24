package com.example.calorie_app_prototype

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val capture_button = findViewById<CardView>(R.id.capture_button)

        capture_button.setOnClickListener {
            // Create an Intent to start the target activity
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }
}