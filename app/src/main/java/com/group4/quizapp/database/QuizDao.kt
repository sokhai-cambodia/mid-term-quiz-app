package com.group4.quizapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuizDao {

    // Questions
    @Query("""
    SELECT * FROM questions WHERE category = :category AND difficulty = :difficulty 
    AND id IN (
        SELECT id FROM questions 
        WHERE category = :category AND difficulty = :difficulty 
        GROUP BY questionText 
        ORDER BY RANDOM() 
        LIMIT 5
    )
""")
    suspend fun getQuestions(category: String, difficulty: String): List<Question>

    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<Question>

    @Insert
    suspend fun insertQuestion(question: Question)

    // Results
    @Insert
    suspend fun insertResult(result: QuizResult)

    @Query("SELECT * FROM quiz_results ORDER BY id DESC")
    suspend fun getAllResults(): List<QuizResult>

    @Query("DELETE FROM quiz_results")
    suspend fun clearAllResults()
    @Query("DELETE FROM questions")
    suspend fun clearAllQuestions()
}