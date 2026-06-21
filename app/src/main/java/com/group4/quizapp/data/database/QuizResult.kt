package com.group4.quizapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class QuizResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val category: String,
    val difficulty: String = "Easy",
    val score: Int,
    val totalQuestions: Int,
    val dateTaken: String,
    val timeSpent: Int = 0
)