package com.group4.quizapp

import androidx.appcompat.app.AppCompatDelegate
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.group4.quizapp.database.QuizDatabase

class MainActivity : AppCompatActivity() {

    private var selectedCategory = "General"
    private var selectedDifficulty = "Easy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Apply saved dark mode preference
        val prefs = getSharedPreferences("QuizAppPrefs", MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("darkMode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        setContentView(R.layout.activity_main)

        // Seed database
        val db = QuizDatabase.getDatabase(this)
        DatabaseSeeder.seedDatabase(db)

        // Category buttons
        findViewById<Button>(R.id.btnScience).setOnClickListener {
            selectedCategory = "Science"
        }
        findViewById<Button>(R.id.btnMath).setOnClickListener {
            selectedCategory = "Math"
        }
        findViewById<Button>(R.id.btnCategoryHistory).setOnClickListener {
            selectedCategory = "History"
        }
        findViewById<Button>(R.id.btnGeneral).setOnClickListener {
            selectedCategory = "General"
        }

        // Difficulty buttons
        findViewById<Button>(R.id.btnEasy).setOnClickListener {
            selectedDifficulty = "Easy"
        }
        findViewById<Button>(R.id.btnMedium).setOnClickListener {
            selectedDifficulty = "Medium"
        }
        findViewById<Button>(R.id.btnHard).setOnClickListener {
            selectedDifficulty = "Hard"
        }

        // Start Quiz button
        findViewById<Button>(R.id.btnStartQuiz).setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("category", selectedCategory)
            intent.putExtra("difficulty", selectedDifficulty)
            startActivity(intent)
        }

        // History navigation button
        findViewById<Button>(R.id.btnHistoryNav).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Settings navigation button
        val btnSettings = findViewById<Button>(R.id.btnSettings)
        btnSettings?.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Exit button
        findViewById<Button>(R.id.btnExit).setOnClickListener {
            finishAffinity()
        }
    }
}