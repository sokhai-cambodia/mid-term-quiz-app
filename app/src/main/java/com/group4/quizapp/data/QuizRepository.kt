package com.group4.quizapp.data

import android.content.Context
import com.group4.quizapp.data.local.QuizDao
import com.group4.quizapp.data.local.DatabaseSeeder
import com.group4.quizapp.data.local.QuizDatabase
import com.group4.quizapp.data.model.Question
import com.group4.quizapp.data.model.QuizAttemptDetail
import com.group4.quizapp.data.model.QuizResult
import kotlinx.coroutines.flow.Flow

class QuizRepository private constructor(private val dao: QuizDao) {

    suspend fun getQuestions(category: String, difficulty: String): List<Question> =
        dao.getQuestions(category, difficulty)

    suspend fun insertResult(result: QuizResult): Long =
        dao.insertResult(result)

    suspend fun insertAttemptDetails(details: List<QuizAttemptDetail>) =
        dao.insertAttemptDetails(details)

    suspend fun getAttemptDetails(resultId: Int): List<QuizAttemptDetail> =
        dao.getAttemptDetails(resultId)

    fun getAllResults(): Flow<List<QuizResult>> =
        dao.getAllResults()

    suspend fun clearResults() = dao.clearResults()

    fun getTopScoresByCategory(): Flow<List<QuizResult>> =
        dao.getTopScoresByCategory()

    fun searchResults(query: String): Flow<List<QuizResult>> =
        dao.searchResults(query)

    suspend fun seedDatabase() {
        dao.clearAllQuestions()
        val questions = DatabaseSeeder.getQuestions()
        questions.forEach { dao.insertQuestion(it) }
    }

    companion object {
        @Volatile
        private var INSTANCE: QuizRepository? = null

        fun getInstance(context: Context): QuizRepository {
            return INSTANCE ?: synchronized(this) {
                val database = QuizDatabase.getDatabase(context)
                val instance = QuizRepository(database.quizDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
