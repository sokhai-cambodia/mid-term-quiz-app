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

    private lateinit var btnScience: Button
    private lateinit var btnMath: Button
    private lateinit var btnHistory: Button
    private lateinit var btnGeneral: Button
    
    private lateinit var btnEasy: Button
    private lateinit var btnMedium: Button
    private lateinit var btnHard: Button

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

        // Initialize buttons
        btnScience = findViewById(R.id.btnScience)
        btnMath = findViewById(R.id.btnMath)
        btnHistory = findViewById(R.id.btnCategoryHistory)
        btnGeneral = findViewById(R.id.btnGeneral)
        
        btnEasy = findViewById(R.id.btnEasy)
        btnMedium = findViewById(R.id.btnMedium)
        btnHard = findViewById(R.id.btnHard)

        // Set initial selection UI
        updateCategoryUI()
        updateDifficultyUI()

        // Seed database
        viewModel.seedDatabase()

        // Category buttons
        btnScience.setOnClickListener {
            selectedCategory = "Science"
            updateCategoryUI()
        }
        btnMath.setOnClickListener {
            selectedCategory = "Math"
            updateCategoryUI()
        }
        btnHistory.setOnClickListener {
            selectedCategory = "History"
            updateCategoryUI()
        }
        btnGeneral.setOnClickListener {
            selectedCategory = "General"
            updateCategoryUI()
        }

        // Difficulty buttons
        btnEasy.setOnClickListener {
            selectedDifficulty = "Easy"
            updateDifficultyUI()
        }
        btnMedium.setOnClickListener {
            selectedDifficulty = "Medium"
            updateDifficultyUI()
        }
        btnHard.setOnClickListener {
            selectedDifficulty = "Hard"
            updateDifficultyUI()
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

    private fun updateCategoryUI() {
        val activeColor = 0xFF1B3A6B.toInt()
        val activeTextColor = 0xFFFFFFFF.toInt()
        val inactiveColor = 0xFFE8EEF8.toInt()
        val inactiveTextColor = 0xFF1B3A6B.toInt()

        val buttons = listOf(btnScience, btnMath, btnHistory, btnGeneral)
        val names = listOf("Science", "Math", "History", "General")

        for (i in buttons.indices) {
            if (names[i] == selectedCategory) {
                buttons[i].setBackgroundColor(activeColor)
                buttons[i].setTextColor(activeTextColor)
            } else {
                buttons[i].setBackgroundColor(inactiveColor)
                buttons[i].setTextColor(inactiveTextColor)
            }
        }
    }

    private fun updateDifficultyUI() {
        // Active colors
        val easyActive = 0xFF2ECC71.toInt()
        val mediumActive = 0xFFF5A623.toInt()
        val hardActive = 0xFFE74C3C.toInt()
        val activeTextColor = 0xFFFFFFFF.toInt()

        // Inactive colors
        val easyInactive = 0xFFE6F7EE.toInt()
        val easyInactiveText = 0xFF1A7A40.toInt()
        val mediumInactive = 0xFFFFF3E0.toInt()
        val mediumInactiveText = 0xFF7A4A00.toInt()
        val hardInactive = 0xFFFFEEEE.toInt()
        val hardInactiveText = 0xFFAA0000.toInt()

        // Reset all to inactive first
        btnEasy.setBackgroundColor(easyInactive)
        btnEasy.setTextColor(easyInactiveText)
        btnMedium.setBackgroundColor(mediumInactive)
        btnMedium.setTextColor(mediumInactiveText)
        btnHard.setBackgroundColor(hardInactive)
        btnHard.setTextColor(hardInactiveText)

        // Highlight selected
        when (selectedDifficulty) {
            "Easy" -> {
                btnEasy.setBackgroundColor(easyActive)
                btnEasy.setTextColor(activeTextColor)
            }
            "Medium" -> {
                btnMedium.setBackgroundColor(mediumActive)
                btnMedium.setTextColor(activeTextColor)
            }
            "Hard" -> {
                btnHard.setBackgroundColor(hardActive)
                btnHard.setTextColor(activeTextColor)
            }
        }
    }
}
