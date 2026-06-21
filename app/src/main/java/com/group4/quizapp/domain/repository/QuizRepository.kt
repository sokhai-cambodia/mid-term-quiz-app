package com.group4.quizapp.domain.repository

import com.group4.quizapp.domain.model.Question
import com.group4.quizapp.domain.model.QuizAttemptDetail
import com.group4.quizapp.domain.model.QuizResult
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    suspend fun getQuestions(category: String, difficulty: String): List<Question>

    suspend fun insertResult(result: QuizResult): Long
    suspend fun insertAttemptDetails(details: List<QuizAttemptDetail>)
    suspend fun getAttemptDetails(resultId: Int): List<QuizAttemptDetail>

    fun getAllResults(): Flow<List<QuizResult>>
    fun searchResults(query: String): Flow<List<QuizResult>>
    fun getTopScoresByCategory(): Flow<List<QuizResult>>
    suspend fun clearResults()

    suspend fun seedQuestions()
}
