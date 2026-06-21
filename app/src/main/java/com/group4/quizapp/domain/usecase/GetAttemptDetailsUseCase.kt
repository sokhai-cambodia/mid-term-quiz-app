package com.group4.quizapp.domain.usecase

import com.group4.quizapp.domain.model.QuizAttemptDetail
import com.group4.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

class GetAttemptDetailsUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(resultId: Int): List<QuizAttemptDetail> =
        repository.getAttemptDetails(resultId)
}
