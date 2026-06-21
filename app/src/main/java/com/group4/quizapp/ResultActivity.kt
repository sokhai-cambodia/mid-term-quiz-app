package com.group4.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.group4.quizapp.database.QuizDatabase
import com.group4.quizapp.database.QuizResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

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

        // Save result to database
        val db = QuizDatabase.getDatabase(this)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val today = dateFormat.format(Date())

        CoroutineScope(Dispatchers.IO).launch {
            db.quizDao().insertResult(
                QuizResult(
                    category = category,
                    difficulty = difficulty,
                    score = score,
                    totalQuestions = total,
                    dateTaken = today,
                    timeSpent = timeSpent
                )
            )
        }

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