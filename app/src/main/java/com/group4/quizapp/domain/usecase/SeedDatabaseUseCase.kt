package com.group4.quizapp.domain.usecase

import com.group4.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

class SeedDatabaseUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke() = repository.seedQuestions()
}
