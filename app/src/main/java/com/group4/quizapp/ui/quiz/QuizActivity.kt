package com.group4.quizapp.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.group4.quizapp.R
import com.group4.quizapp.data.database.Question
import com.group4.quizapp.ui.result.ResultActivity
import com.group4.quizapp.utils.PreferencesManager

class QuizActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvTimer: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnOptionA: Button
    private lateinit var btnOptionB: Button
    private lateinit var btnOptionC: Button
    private lateinit var btnOptionD: Button

    private val viewModel: QuizViewModel by viewModels()
    private var startTime: Long = 0
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

        setupObservers()
        viewModel.loadQuestions(category, difficulty)

        btnOptionA.setOnClickListener { checkAnswer("A") }
        btnOptionB.setOnClickListener { checkAnswer("B") }
        btnOptionC.setOnClickListener { checkAnswer("C") }
        btnOptionD.setOnClickListener { checkAnswer("D") }
    }

    private fun setupObservers() {
        viewModel.questions.observe(this) { questions ->
            if (questions.isEmpty()) {
                tvQuestion.text = "No questions found for this category and difficulty!"
            } else {
                if (viewModel.currentIndex.value == 0) {
                    startTime = System.currentTimeMillis()
                }
            }
        }

        viewModel.currentIndex.observe(this) { index ->
            val questions = viewModel.questions.value ?: return@observe
            if (index < questions.size) {
                showQuestion(questions[index], index, questions.size)
            }
        }

        viewModel.quizFinished.observe(this) { finished ->
            if (finished) {
                val timeSpent = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("score", viewModel.score.value ?: 0)
                intent.putExtra("total", viewModel.questions.value?.size ?: 0)
                intent.putExtra("category", category)
                intent.putExtra("difficulty", difficulty)
                intent.putExtra("timeSpent", timeSpent)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showQuestion(question: Question, index: Int, total: Int) {
        timer?.cancel()
        tvQuestionNumber.text = "Question ${index + 1} of $total"
        progressBar.progress = ((index + 1) * 100 / total)

        tvQuestion.text = question.questionText
        btnOptionA.text = "A.  ${question.optionA}"
        btnOptionB.text = "B.  ${question.optionB}"
        btnOptionC.text = "C.  ${question.optionC}"
        btnOptionD.text = "D.  ${question.optionD}"

        resetButtonColors()
        startTimer()
    }

    private fun startTimer() {
        val prefs = PreferencesManager(this)
        val timerSeconds = prefs.timerDuration
        timer = object : CountDownTimer(timerSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTimer.text = "${millisUntilFinished / 1000}s"
            }
            override fun onFinish() {
                tvTimer.text = "0s"
                viewModel.answerQuestion(false)
            }
        }.start()
    }

    private fun checkAnswer(selected: String) {
        val questions = viewModel.questions.value ?: return
        val currentIndex = viewModel.currentIndex.value ?: 0
        if (currentIndex >= questions.size) return
        
        timer?.cancel()
        val correct = questions[currentIndex].correctOption
        val isCorrect = selected == correct

        if (isCorrect) {
            getButtonByOption(selected).setBackgroundColor(0xFF2ECC71.toInt())
        } else {
            getButtonByOption(selected).setBackgroundColor(0xFFE74C3C.toInt())
            getButtonByOption(correct).setBackgroundColor(0xFF2ECC71.toInt())
        }

        tvQuestion.postDelayed({ 
            viewModel.answerQuestion(isCorrect)
        }, 1000)
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
