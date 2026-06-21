package com.group4.quizapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_attempt_details",
    foreignKeys = [
        ForeignKey(
            entity = QuizResultEntity::class,
            parentColumns = ["id"],
            childColumns = ["resultId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["resultId"])]
)
data class QuizAttemptDetailEntity(
    @PrimaryKey(autoGenerate = true)
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
