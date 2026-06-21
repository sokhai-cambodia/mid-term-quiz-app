package com.group4.quizapp.data.repository

import com.group4.quizapp.data.database.QuizDao
import com.group4.quizapp.data.database.QuizResult

class QuizRepository(private val dao: QuizDao) {
    suspend fun getQuestions(category: String, difficulty: String) =
        dao.getQuestions(category, difficulty)

    suspend fun insertResult(result: QuizResult) =
        dao.insertResult(result)

    suspend fun getAllResults(): List<QuizResult> =
        dao.getAllResults()

    suspend fun clearResults() =
        dao.clearResults()

    suspend fun getTopScoresByCategory(): List<QuizResult> =
        dao.getTopScoresByCategory()

    suspend fun searchResults(query: String): List<QuizResult> =
        dao.searchResults(query)
}
