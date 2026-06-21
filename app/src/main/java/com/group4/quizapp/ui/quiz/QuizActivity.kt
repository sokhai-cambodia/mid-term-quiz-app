package com.group4.quizapp.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
    private var lastShownIndex = -1
    private var isAnswered = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Handle window insets for safe header and footer
        val rootLayout = findViewById<android.view.ViewGroup>(R.id.quizRoot)
        val header = findViewById<android.view.ViewGroup>(R.id.quizHeader)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            header.updatePadding(top = systemBars.top)
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

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

        btnOptionA.setOnClickListener { if (!isAnswered) checkAnswer("A") }
        btnOptionB.setOnClickListener { if (!isAnswered) checkAnswer("B") }
        btnOptionC.setOnClickListener { if (!isAnswered) checkAnswer("C") }
        btnOptionD.setOnClickListener { if (!isAnswered) checkAnswer("D") }
    }

    private fun setupObservers() {
        // Observe questions
        viewModel.questions.observe(this) { questions ->
            if (questions.isEmpty()) {
                tvQuestion.text = "No questions found for this category and difficulty!"
            } else {
                if (startTime == 0L) {
                    startTime = System.currentTimeMillis()
                }
                val index = viewModel.currentIndex.value ?: 0
                if (index < questions.size) {
                    showQuestion(questions[index], index, questions.size)
                }
            }
        }

        // Observe current index changes
        viewModel.currentIndex.observe(this) { index ->
            val questions = viewModel.questions.value ?: return@observe
            if (index < questions.size) {
                showQuestion(questions[index], index, questions.size)
            }
        }

        viewModel.quizFinished.observe(this) { finished ->
            if (finished) {
                val timeSpent = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                
                // Save results with details before finishing
                viewModel.saveResults(category, difficulty, timeSpent)
                
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
        if (index == lastShownIndex) return
        lastShownIndex = index
        isAnswered = false

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
                if (!isAnswered) {
                    checkAnswer("None")
                }
            }
        }.start()
    }

    private fun checkAnswer(selected: String) {
        if (isAnswered) return
        isAnswered = true

        val questions = viewModel.questions.value ?: return
        val currentIndex = viewModel.currentIndex.value ?: 0
        if (currentIndex >= questions.size) return
        
        timer?.cancel()
        val correct = questions[currentIndex].correctOption
        val isCorrect = selected == correct

        // Set text color to white for better visibility on result colors
        val white = ContextCompat.getColor(this, android.R.color.white)
        
        if (selected != "None") {
            if (isCorrect) {
                val button = getButtonByOption(selected)
                button.setBackgroundColor(0xFF2ECC71.toInt())
                button.setTextColor(white)
            } else {
                val selectedButton = getButtonByOption(selected)
                val correctButton = getButtonByOption(correct)
                
                selectedButton.setBackgroundColor(0xFFE74C3C.toInt())
                selectedButton.setTextColor(white)
                
                correctButton.setBackgroundColor(0xFF2ECC71.toInt())
                correctButton.setTextColor(white)
            }
        } else {
            // Show correct answer even if time ran out
            val correctButton = getButtonByOption(correct)
            correctButton.setBackgroundColor(0xFF2ECC71.toInt())
            correctButton.setTextColor(white)
        }

        tvQuestion.postDelayed({ 
            viewModel.answerQuestion(selected)
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
        val cardBg = ContextCompat.getColor(this, R.color.cardBackgroundColor)
        val primaryText = ContextCompat.getColor(this, R.color.primaryTextColor)
        
        val buttons = listOf(btnOptionA, btnOptionB, btnOptionC, btnOptionD)
        for (button in buttons) {
            button.setBackgroundColor(cardBg)
            button.setTextColor(primaryText)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
