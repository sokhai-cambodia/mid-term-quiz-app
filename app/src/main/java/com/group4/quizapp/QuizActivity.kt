package com.group4.quizapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.group4.quizapp.database.Question
import com.group4.quizapp.database.QuizDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvTimer: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnOptionA: Button
    private lateinit var btnOptionB: Button
    private lateinit var btnOptionC: Button
    private lateinit var btnOptionD: Button

    private var startTime: Long = 0
    private var questions = listOf<Question>()
    private var currentIndex = 0
    private var score = 0
    private var answered = false
    private var timer: CountDownTimer? = null
    private var category = "General"
    private var difficulty = "Easy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        category = intent.getStringExtra("category") ?: "General"
        difficulty = intent.getStringExtra("difficulty") ?: "Easy"

        tvQuestion = findViewById(R.id.tvQuestion)
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber)
        tvTimer = findViewById(R.id.tvTimer)
        progressBar = findViewById(R.id.progressBar)
        btnOptionA = findViewById(R.id.btnOptionA)
        btnOptionB = findViewById(R.id.btnOptionB)
        btnOptionC = findViewById(R.id.btnOptionC)
        btnOptionD = findViewById(R.id.btnOptionD)

        loadQuestions()

        btnOptionA.setOnClickListener {
            if (questions.isNotEmpty() && !answered) checkAnswer("A")
        }
        btnOptionB.setOnClickListener {
            if (questions.isNotEmpty() && !answered) checkAnswer("B")
        }
        btnOptionC.setOnClickListener {
            if (questions.isNotEmpty() && !answered) checkAnswer("C")
        }
        btnOptionD.setOnClickListener {
            if (questions.isNotEmpty() && !answered) checkAnswer("D")
        }
    }

    private fun loadQuestions() {
        val db = QuizDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            questions = db.quizDao().getQuestions(category, difficulty)
            withContext(Dispatchers.Main) {
                if (questions.isEmpty()) {
                    tvQuestion.text = "No questions found for this category and difficulty!"
                } else {
                    showQuestion()

                    if (currentIndex == 0) {
                        startTime = System.currentTimeMillis()
                    }
                }
            }
        }
    }

    private fun showQuestion() {
        timer?.cancel()
        answered = false
        val question = questions[currentIndex]

        tvQuestionNumber.text = "Question ${currentIndex + 1} of ${questions.size}"
        progressBar.progress = ((currentIndex + 1) * 100 / questions.size)

        tvQuestion.text = question.questionText
        btnOptionA.text = "A.  ${question.optionA}"
        btnOptionB.text = "B.  ${question.optionB}"
        btnOptionC.text = "C.  ${question.optionC}"
        btnOptionD.text = "D.  ${question.optionD}"

        resetButtonColors()
        startTimer()
    }

    private fun startTimer() {
        val prefs = getSharedPreferences("QuizAppPrefs", MODE_PRIVATE)
        val timerSeconds = prefs.getInt("timerDuration", 30)
        timer = object : CountDownTimer(timerSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "${millisUntilFinished / 1000}s"
            }
            override fun onFinish() {
                tvTimer.text = "0s"
                if (!answered) moveToNext()
            }
        }.start()
    }

    private fun checkAnswer(selected: String) {
        if (answered) return
        answered = true
        timer?.cancel()

        val correct = questions[currentIndex].correctOption

        if (selected == correct) {
            score++
            getButtonByOption(selected).setBackgroundColor(0xFF2ECC71.toInt())
        } else {
            getButtonByOption(selected).setBackgroundColor(0xFFE74C3C.toInt())
            getButtonByOption(correct).setBackgroundColor(0xFF2ECC71.toInt())
        }

        tvQuestion.postDelayed({ moveToNext() }, 1000)
    }

    private fun moveToNext() {
        currentIndex++
        if (currentIndex < questions.size) {
            showQuestion()
        } else {
            val timeSpent = ((System.currentTimeMillis() - startTime) / 1000).toInt()
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("score", score)
            intent.putExtra("total", questions.size)
            intent.putExtra("category", category)
            intent.putExtra("difficulty", difficulty)
            intent.putExtra("timeSpent", timeSpent)
            startActivity(intent)
            finish()
        }
    }

    private fun getButtonByOption(option: String): Button {
        return when (option) {
            "A" -> btnOptionA
            "B" -> btnOptionB
            "C" -> btnOptionC
            else -> btnOptionD
        }
    }

    private fun resetButtonColors() {
        val white = 0xFFFFFFFF.toInt()
        btnOptionA.setBackgroundColor(white)
        btnOptionB.setBackgroundColor(white)
        btnOptionC.setBackgroundColor(white)
        btnOptionD.setBackgroundColor(white)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}