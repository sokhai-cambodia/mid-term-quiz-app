package com.group4.quizapp.ui.quiz

import android.content.Intent
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.group4.quizapp.R
import com.group4.quizapp.domain.model.Question
import com.group4.quizapp.databinding.ActivityQuizBinding
import com.group4.quizapp.ui.base.BaseActivity
import com.group4.quizapp.ui.result.ResultActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuizActivity : BaseActivity<ActivityQuizBinding>(ActivityQuizBinding::inflate) {

    private val viewModel: QuizViewModel by viewModels()
    private var startTime: Long = 0
    private var timer: CountDownTimer? = null
    private var category = "General"
    private var difficulty = "Easy"
    private var lastShownIndex = -1
    private var isAnswered = false

    override fun getHeaderView(): View = binding.quizHeader

    override fun initViews() {
        category = intent.getStringExtra("category") ?: "General"
        difficulty = intent.getStringExtra("difficulty") ?: "Easy"

        viewModel.loadQuestions(category, difficulty)

        binding.apply {
            btnOptionA.setOnClickListener { if (!isAnswered) checkAnswer("A") }
            btnOptionB.setOnClickListener { if (!isAnswered) checkAnswer("B") }
            btnOptionC.setOnClickListener { if (!isAnswered) checkAnswer("C") }
            btnOptionD.setOnClickListener { if (!isAnswered) checkAnswer("D") }
        }
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        if (state.isLoading) return@collect

                        if (state.errorMessage != null) {
                            binding.tvQuestion.text = state.errorMessage
                            return@collect
                        }

                        if (state.isFinished) {
                            val timeSpent = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                            viewModel.saveResults(category, difficulty, timeSpent)

                            val intent = Intent(this@QuizActivity, ResultActivity::class.java)
                            intent.putExtra("score", state.score)
                            intent.putExtra("total", state.questions.size)
                            intent.putExtra("category", category)
                            intent.putExtra("difficulty", difficulty)
                            intent.putExtra("timeSpent", timeSpent)
                            startActivity(intent)
                            finish()
                            return@collect
                        }

                        if (state.questions.isNotEmpty()) {
                            if (startTime == 0L) startTime = System.currentTimeMillis()
                            showQuestion(state.questions[state.currentIndex], state.currentIndex, state.questions.size)
                        }
                    }
                }
            }
        }
    }

    private fun showQuestion(question: Question, index: Int, total: Int) {
        if (index == lastShownIndex) return
        lastShownIndex = index
        isAnswered = false

        timer?.cancel()
        binding.apply {
            tvQuestionNumber.text = getString(R.string.question_progress, index + 1, total)
            progressBar.progress = ((index + 1) * 100 / total)

            tvQuestion.text = question.questionText
            btnOptionA.text = getString(R.string.option_a_format, question.optionA)
            btnOptionB.text = getString(R.string.option_b_format, question.optionB)
            btnOptionC.text = getString(R.string.option_c_format, question.optionC)
            btnOptionD.text = getString(R.string.option_d_format, question.optionD)

            resetButtonColors()
        }
        startTimer()
    }

    private fun startTimer() {
        val timerSeconds = prefs.timerDuration
        timer = object : CountDownTimer(timerSeconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = getString(R.string.timer_format, (millisUntilFinished / 1000).toInt())
            }
            override fun onFinish() {
                binding.tvTimer.text = "0s"
                if (!isAnswered) {
                    checkAnswer("None")
                }
            }
        }.start()
    }

    private fun checkAnswer(selected: String) {
        if (isAnswered) return
        isAnswered = true

        val state = viewModel.uiState.value ?: return
        val questions = state.questions
        val currentIndex = state.currentIndex
        if (currentIndex >= questions.size) return
        
        timer?.cancel()
        val correct = questions[currentIndex].correctOption
        val isCorrect = selected == correct

        val white = ContextCompat.getColor(this, android.R.color.white)
        
        binding.apply {
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
                val correctButton = getButtonByOption(correct)
                correctButton.setBackgroundColor(0xFF2ECC71.toInt())
                correctButton.setTextColor(white)
            }
        }

        binding.tvQuestion.postDelayed({ 
            viewModel.answerQuestion(selected)
        }, 1000)
    }

    private fun getButtonByOption(option: String): Button {
        return when (option) {
            "A" -> binding.btnOptionA
            "B" -> binding.btnOptionB
            "C" -> binding.btnOptionC
            else -> binding.btnOptionD
        }
    }

    private fun resetButtonColors() {
        val cardBg = ContextCompat.getColor(this, R.color.cardBackgroundColor)
        val primaryText = ContextCompat.getColor(this, R.color.primaryTextColor)
        
        binding.apply {
            val buttons = listOf(btnOptionA, btnOptionB, btnOptionC, btnOptionD)
            for (button in buttons) {
                button.setBackgroundColor(cardBg)
                button.setTextColor(primaryText)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
