package com.group4.quizapp.domain.model

data class QuizAttemptDetail(
    val id: Int = 0,
    val resultId: Int,
    val questionText: String,
    val selectedOption: String,
    val correctOption: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String
)
