package com.group4.quizapp.domain.model

data class QuizResult(
    val id: Int = 0,
    val category: String,
    val difficulty: String = "Easy",
    val score: Int,
    val totalQuestions: Int,
    val dateTaken: String,
    val timeSpent: Int = 0
)
