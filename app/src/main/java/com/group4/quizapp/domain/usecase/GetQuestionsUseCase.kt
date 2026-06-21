package com.group4.quizapp.domain.usecase

import com.group4.quizapp.domain.model.Question
import com.group4.quizapp.domain.repository.QuizRepository
import javax.inject.Inject

class GetQuestionsUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(category: String, difficulty: String): List<Question> =
        repository.getQuestions(category, difficulty)
}
