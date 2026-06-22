package com.group4.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.group4.quizapp.data.model.Question
import com.group4.quizapp.data.model.QuizAttemptDetail
import com.group4.quizapp.data.model.QuizResult
import kotlinx.coroutines.flow.Flow

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

    @Insert
    suspend fun insertQuestion(question: Question)

    @Query("DELETE FROM questions")
    suspend fun clearAllQuestions()

    // Results
    @Insert
    suspend fun insertResult(result: QuizResult): Long

    @Insert
    suspend fun insertAttemptDetails(details: List<QuizAttemptDetail>)

    @Query("SELECT * FROM quiz_attempt_details WHERE resultId = :resultId")
    suspend fun getAttemptDetails(resultId: Int): List<QuizAttemptDetail>

    @Query("SELECT * FROM quiz_results ORDER BY id DESC")
    fun getAllResults(): Flow<List<QuizResult>>

    @Query("DELETE FROM quiz_results")
    suspend fun clearResults()

    @Query("SELECT * FROM quiz_results ORDER BY score DESC")
    fun getTopScoresByCategory(): Flow<List<QuizResult>>

    @Query("SELECT * FROM quiz_results WHERE category LIKE '%' || :query || '%' OR difficulty LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchResults(query: String): Flow<List<QuizResult>>
}
