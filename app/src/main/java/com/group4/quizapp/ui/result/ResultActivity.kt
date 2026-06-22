package com.group4.quizapp.ui.result

import android.content.Intent
import android.view.View
import com.group4.quizapp.R
import com.group4.quizapp.databinding.ActivityResultBinding
import com.group4.quizapp.ui.base.BaseActivity
import com.group4.quizapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultActivity : BaseActivity<ActivityResultBinding>(ActivityResultBinding::inflate) {

    override fun getHeaderView(): View = binding.resultHeader

    override fun initViews() {
        val score = intent.getIntExtra("score", 0)
        val total = intent.getIntExtra("total", 0)
        val category = intent.getStringExtra("category") ?: "General"
        val difficulty = intent.getStringExtra("difficulty") ?: "Easy"
        val timeSpent = intent.getIntExtra("timeSpent", 0)
        
        val minutes = timeSpent / 60
        val seconds = timeSpent % 60
        val timeText = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"

        binding.apply {
            tvScore.text = getString(R.string.score_format, score, total)
            tvCategory.text = getString(R.string.result_summary_format, category, difficulty, timeText)

            val percentage = if (total > 0) (score * 100 / total) else 0
            tvResult.text = when {
                percentage >= 80 -> "Excellent! 🎉"
                percentage >= 60 -> "Great job! 👍"
                percentage >= 40 -> "Good effort! 😊"
                else -> "Keep practicing! 💪"
            }

            btnRetake.setOnClickListener { finish() }
            btnGoHome.setOnClickListener {
                val intent = Intent(this@ResultActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
    }
}
