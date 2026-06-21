package com.group4.quizapp.ui.result

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.group4.quizapp.R
import com.group4.quizapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Handle window insets for safe header and footer
        val rootLayout = findViewById<android.view.ViewGroup>(R.id.resultRoot)
        val header = findViewById<android.view.ViewGroup>(R.id.resultHeader)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            header.updatePadding(top = systemBars.top)
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        // Get data from QuizActivity
        val score = intent.getIntExtra("score", 0)
        val total = intent.getIntExtra("total", 0)
        val category = intent.getStringExtra("category") ?: "General"
        val difficulty = intent.getStringExtra("difficulty") ?: "Easy"
        val timeSpent = intent.getIntExtra("timeSpent", 0)
        val minutes = timeSpent / 60
        val seconds = timeSpent % 60
        val timeText = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"

        // Connect views
        val tvScore = findViewById<TextView>(R.id.tvScore)
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        val btnRetake = findViewById<Button>(R.id.btnRetake)
        val btnGoHome = findViewById<Button>(R.id.btnGoHome)

        // Show score
        tvScore.text = "$score/$total"
        tvCategory.text = "Category: $category  ·  $difficulty  ·  $timeText"

        // Show result message
        val percentage = if (total > 0) (score * 100 / total) else 0
        tvResult.text = when {
            percentage >= 80 -> "Excellent! 🎉"
            percentage >= 60 -> "Great job! 👍"
            percentage >= 40 -> "Good effort! 😊"
            else -> "Keep practicing! 💪"
        }

        // Result saving is now handled in QuizActivity/QuizViewModel for detail capture
        // So we don't save it here anymore to avoid duplicates.

        // Button clicks
        btnRetake.setOnClickListener {
            finish()
        }

        btnGoHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}
