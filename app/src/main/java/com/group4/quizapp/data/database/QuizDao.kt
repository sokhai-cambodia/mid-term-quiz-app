package com.group4.quizapp.data.database

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
    suspend fun insertResult(result: QuizResult): Long

    @Insert
    suspend fun insertAttemptDetails(details: List<QuizAttemptDetail>)

    @Query("SELECT * FROM quiz_attempt_details WHERE resultId = :resultId")
    suspend fun getAttemptDetails(resultId: Int): List<QuizAttemptDetail>

    @Query("SELECT * FROM quiz_results ORDER BY id DESC")
    suspend fun getAllResults(): List<QuizResult>

    @Query("DELETE FROM quiz_results")
    suspend fun clearResults()

    // For Leaderboard — top score per category
    @Query("SELECT * FROM quiz_results ORDER BY score DESC")
    suspend fun getTopScoresByCategory(): List<QuizResult>

    // For Search/Filter on History
    @Query("SELECT * FROM quiz_results WHERE category LIKE '%' || :query || '%' OR difficulty LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun searchResults(query: String): List<QuizResult>

    @Query("DELETE FROM questions")
    suspend fun clearAllQuestions()
}