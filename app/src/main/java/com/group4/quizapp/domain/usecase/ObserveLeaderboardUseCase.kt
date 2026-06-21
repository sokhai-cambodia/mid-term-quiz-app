package com.group4.quizapp.domain.usecase

import com.group4.quizapp.domain.model.QuizResult
import com.group4.quizapp.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLeaderboardUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(): Flow<List<QuizResult>> = repository.getTopScoresByCategory()
}
