package com.group4.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.group4.quizapp.data.local.entity.QuestionEntity
import com.group4.quizapp.data.local.entity.QuizAttemptDetailEntity
import com.group4.quizapp.data.local.entity.QuizResultEntity
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
    suspend fun getQuestions(category: String, difficulty: String): List<QuestionEntity>

    @Insert
    suspend fun insertQuestion(question: QuestionEntity)

    // Results
    @Insert
    suspend fun insertResult(result: QuizResultEntity): Long

    @Insert
    suspend fun insertAttemptDetails(details: List<QuizAttemptDetailEntity>)

    @Query("SELECT * FROM quiz_attempt_details WHERE resultId = :resultId")
    suspend fun getAttemptDetails(resultId: Int): List<QuizAttemptDetailEntity>

    @Query("SELECT * FROM quiz_results ORDER BY id DESC")
    fun getAllResults(): Flow<List<QuizResultEntity>>

    @Query("DELETE FROM quiz_results")
    suspend fun clearResults()

    // For Leaderboard — top score per category
    @Query("SELECT * FROM quiz_results ORDER BY score DESC")
    fun getTopScoresByCategory(): Flow<List<QuizResultEntity>>

    // For Search/Filter on History
    @Query("SELECT * FROM quiz_results WHERE category LIKE '%' || :query || '%' OR difficulty LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchResults(query: String): Flow<List<QuizResultEntity>>

    @Query("DELETE FROM questions")
    suspend fun clearAllQuestions()
}
