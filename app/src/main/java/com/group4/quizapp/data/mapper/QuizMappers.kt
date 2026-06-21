package com.group4.quizapp.data.mapper

import com.group4.quizapp.data.local.entity.QuestionEntity
import com.group4.quizapp.data.local.entity.QuizAttemptDetailEntity
import com.group4.quizapp.data.local.entity.QuizResultEntity
import com.group4.quizapp.domain.model.Question
import com.group4.quizapp.domain.model.QuizAttemptDetail
import com.group4.quizapp.domain.model.QuizResult

fun QuestionEntity.toDomain() = Question(
    id = id,
    questionText = questionText,
    optionA = optionA,
    optionB = optionB,
    optionC = optionC,
    optionD = optionD,
    correctOption = correctOption,
    category = category,
    difficulty = difficulty
)

fun Question.toEntity() = QuestionEntity(
    id = id,
    questionText = questionText,
    optionA = optionA,
    optionB = optionB,
    optionC = optionC,
    optionD = optionD,
    correctOption = correctOption,
    category = category,
    difficulty = difficulty
)

fun QuizResultEntity.toDomain() = QuizResult(
    id = id,
    category = category,
    difficulty = difficulty,
    score = score,
    totalQuestions = totalQuestions,
    dateTaken = dateTaken,
    timeSpent = timeSpent
)

fun QuizResult.toEntity() = QuizResultEntity(
    id = id,
    category = category,
    difficulty = difficulty,
    score = score,
    totalQuestions = totalQuestions,
    dateTaken = dateTaken,
    timeSpent = timeSpent
)

fun QuizAttemptDetailEntity.toDomain() = QuizAttemptDetail(
    id = id,
    resultId = resultId,
    questionText = questionText,
    selectedOption = selectedOption,
    correctOption = correctOption,
    optionA = optionA,
    optionB = optionB,
    optionC = optionC,
    optionD = optionD
)

fun QuizAttemptDetail.toEntity() = QuizAttemptDetailEntity(
    id = id,
    resultId = resultId,
    questionText = questionText,
    selectedOption = selectedOption,
    correctOption = correctOption,
    optionA = optionA,
    optionB = optionB,
    optionC = optionC,
    optionD = optionD
)
