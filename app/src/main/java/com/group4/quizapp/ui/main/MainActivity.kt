package com.group4.quizapp.ui.main

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.group4.quizapp.databinding.ActivityMainBinding
import com.group4.quizapp.ui.base.BaseActivity
import com.group4.quizapp.ui.history.HistoryActivity
import com.group4.quizapp.ui.leaderboard.LeaderboardActivity
import com.group4.quizapp.ui.quiz.QuizActivity
import com.group4.quizapp.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()
    private var selectedCategory = "General"
    private var selectedDifficulty = "Easy"

    override fun getHeaderView(): View = binding.mainHeader

    override fun initViews() {
        // Seed database
        viewModel.seedDatabase()

        // Set initial selection UI
        updateCategoryUI()
        updateDifficultyUI()

        binding.apply {
            // Category buttons
            btnScience.setOnClickListener {
                selectedCategory = "Science"
                updateCategoryUI()
            }
            btnMath.setOnClickListener {
                selectedCategory = "Math"
                updateCategoryUI()
            }
            btnCategoryHistory.setOnClickListener {
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
            btnStartQuiz.setOnClickListener {
                val intent = Intent(this@MainActivity, QuizActivity::class.java)
                intent.putExtra("category", selectedCategory)
                intent.putExtra("difficulty", selectedDifficulty)
                startActivity(intent)
            }

            // History navigation button
            btnHistoryNav.setOnClickListener {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
            }

            // Leaderboard navigation button
            btnLeaderboard.setOnClickListener {
                startActivity(Intent(this@MainActivity, LeaderboardActivity::class.java))
            }

            // Settings navigation button
            btnSettings.setOnClickListener {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }

            // Exit button
            btnExit.setOnClickListener {
                finishAffinity()
            }
        }
    }

    private fun updateCategoryUI() {
        val activeColor = 0xFF1B3A6B.toInt()
        val activeTextColor = 0xFFFFFFFF.toInt()
        val inactiveColor = 0xFFE8EEF8.toInt()
        val inactiveTextColor = 0xFF1B3A6B.toInt()

        val buttons = listOf(binding.btnScience, binding.btnMath, binding.btnCategoryHistory, binding.btnGeneral)
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
        val activeTextColor = 0xFFFFFFFF.toInt()
        
        binding.apply {
            btnEasy.setBackgroundColor(0xFFE6F7EE.toInt())
            btnEasy.setTextColor(0xFF1A7A40.toInt())
            btnMedium.setBackgroundColor(0xFFFFF3E0.toInt())
            btnMedium.setTextColor(0xFF7A4A00.toInt())
            btnHard.setBackgroundColor(0xFFFFEEEE.toInt())
            btnHard.setTextColor(0xFFAA0000.toInt())

            when (selectedDifficulty) {
                "Easy" -> {
                    btnEasy.setBackgroundColor(0xFF2ECC71.toInt())
                    btnEasy.setTextColor(activeTextColor)
                }
                "Medium" -> {
                    btnMedium.setBackgroundColor(0xFFF5A623.toInt())
                    btnMedium.setTextColor(activeTextColor)
                }
                "Hard" -> {
                    btnHard.setBackgroundColor(0xFFE74C3C.toInt())
                    btnHard.setTextColor(activeTextColor)
                }
            }
        }
    }
}
