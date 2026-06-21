package com.group4.quizapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.group4.quizapp.R
import com.group4.quizapp.ui.history.HistoryActivity
import com.group4.quizapp.ui.leaderboard.LeaderboardActivity
import com.group4.quizapp.ui.quiz.QuizActivity
import com.group4.quizapp.ui.settings.SettingsActivity
import com.group4.quizapp.utils.PreferencesManager

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var selectedCategory = "General"
    private var selectedDifficulty = "Easy"

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Apply saved dark mode preference
        val prefs = PreferencesManager(this)
        if (prefs.isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        
        setContentView(R.layout.activity_main)

        // Handle window insets for safe header and footer
        val rootLayout = findViewById<android.view.ViewGroup>(R.id.mainRoot)
        val header = findViewById<android.view.ViewGroup>(R.id.mainHeader)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            header.updatePadding(top = systemBars.top)
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        // Seed database
        viewModel.seedDatabase()

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

        // Leaderboard navigation button
        findViewById<Button>(R.id.btnLeaderboard).setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
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
