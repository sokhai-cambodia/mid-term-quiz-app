package com.group4.quizapp.domain.model

data class Question(
    val id: Int = 0,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val category: String,
    val difficulty: String
)
