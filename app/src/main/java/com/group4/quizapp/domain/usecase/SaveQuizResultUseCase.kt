package com.group4.quizapp.domain.usecase

import com.group4.quizapp.domain.model.Question
import com.group4.quizapp.domain.model.QuizAttemptDetail
import com.group4.quizapp.domain.model.QuizResult
import com.group4.quizapp.domain.repository.QuizRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class SaveQuizResultUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(
        category: String,
        difficulty: String,
        score: Int,
        questions: List<Question>,
        userAnswers: List<String>,
        timeSpent: Int
    ) {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val result = QuizResult(
            category = category,
            difficulty = difficulty,
            score = score,
            totalQuestions = questions.size,
            dateTaken = dateFormat.format(Date()),
            timeSpent = timeSpent
        )

        val resultId = repository.insertResult(result).toInt()

        val details = questions.mapIndexed { index, question ->
            QuizAttemptDetail(
                resultId = resultId,
                questionText = question.questionText,
                selectedOption = userAnswers.getOrElse(index) { "None" },
                correctOption = question.correctOption,
                optionA = question.optionA,
                optionB = question.optionB,
                optionC = question.optionC,
                optionD = question.optionD
            )
        }

        repository.insertAttemptDetails(details)
    }
}
