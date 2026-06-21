package com.group4.quizapp.data.repository

import com.group4.quizapp.data.local.QuizDao
import com.group4.quizapp.data.mapper.toDomain
import com.group4.quizapp.data.mapper.toEntity
import com.group4.quizapp.data.seed.DatabaseSeeder
import com.group4.quizapp.domain.model.Question
import com.group4.quizapp.domain.model.QuizAttemptDetail
import com.group4.quizapp.domain.model.QuizResult
import com.group4.quizapp.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val dao: QuizDao
) : QuizRepository {

    override suspend fun getQuestions(category: String, difficulty: String): List<Question> =
        dao.getQuestions(category, difficulty).map { it.toDomain() }

    override suspend fun insertResult(result: QuizResult): Long =
        dao.insertResult(result.toEntity())

    override suspend fun insertAttemptDetails(details: List<QuizAttemptDetail>) =
        dao.insertAttemptDetails(details.map { it.toEntity() })

    override suspend fun getAttemptDetails(resultId: Int): List<QuizAttemptDetail> =
        dao.getAttemptDetails(resultId).map { it.toDomain() }

    override fun getAllResults(): Flow<List<QuizResult>> =
        dao.getAllResults().map { list -> list.map { it.toDomain() } }

    override fun searchResults(query: String): Flow<List<QuizResult>> =
        dao.searchResults(query).map { list -> list.map { it.toDomain() } }

    override fun getTopScoresByCategory(): Flow<List<QuizResult>> =
        dao.getTopScoresByCategory().map { list -> list.map { it.toDomain() } }

    override suspend fun clearResults() = dao.clearResults()

    override suspend fun seedQuestions() {
        dao.clearAllQuestions()
        DatabaseSeeder.seedQuestions().forEach { dao.insertQuestion(it.toEntity()) }
    }
}
