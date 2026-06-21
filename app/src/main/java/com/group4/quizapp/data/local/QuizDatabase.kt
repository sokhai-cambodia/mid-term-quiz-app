package com.group4.quizapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.group4.quizapp.data.local.entity.QuestionEntity
import com.group4.quizapp.data.local.entity.QuizAttemptDetailEntity
import com.group4.quizapp.data.local.entity.QuizResultEntity

@Database(
    entities = [QuestionEntity::class, QuizResultEntity::class, QuizAttemptDetailEntity::class],
    version = 6,
    exportSchema = false
)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
}
