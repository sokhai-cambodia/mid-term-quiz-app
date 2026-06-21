package com.group4.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.group4.quizapp.database.QuizDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switchDarkMode = findViewById<SwitchCompat>(R.id.switchDarkMode)
        val btn15s = findViewById<Button>(R.id.btn15s)
        val btn30s = findViewById<Button>(R.id.btn30s)
        val btn60s = findViewById<Button>(R.id.btn60s)
        val btnClearAllHistory = findViewById<Button>(R.id.btnClearAllHistory)

        // Load saved preferences
        val prefs = getSharedPreferences("QuizAppPrefs", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("darkMode", false)
        switchDarkMode.isChecked = isDarkMode

        // Dark mode toggle
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("darkMode", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Timer buttons
        btn15s.setOnClickListener {
            prefs.edit().putInt("timerDuration", 15).apply()
            Toast.makeText(this, "Timer set to 15 seconds", Toast.LENGTH_SHORT).show()
        }
        btn30s.setOnClickListener {
            prefs.edit().putInt("timerDuration", 30).apply()
            Toast.makeText(this, "Timer set to 30 seconds", Toast.LENGTH_SHORT).show()
        }
        btn60s.setOnClickListener {
            prefs.edit().putInt("timerDuration", 60).apply()
            Toast.makeText(this, "Timer set to 60 seconds", Toast.LENGTH_SHORT).show()
        }
        val btn90s = findViewById<Button>(R.id.btn90s)
        btn90s.setOnClickListener {
            prefs.edit().putInt("timerDuration", 90).apply()
            Toast.makeText(this, "Timer set to 90 seconds", Toast.LENGTH_SHORT).show()
        }

        // Clear all history
        btnClearAllHistory.setOnClickListener {
            val db = QuizDatabase.getDatabase(this)
            CoroutineScope(Dispatchers.IO).launch {
                db.quizDao().clearAllResults()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SettingsActivity,
                        "All history cleared!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
              }
            }
        // Go Home button
        val btnGoHomeSettings = findViewById<Button>(R.id.btnGoHomeSettings)
        btnGoHomeSettings.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}