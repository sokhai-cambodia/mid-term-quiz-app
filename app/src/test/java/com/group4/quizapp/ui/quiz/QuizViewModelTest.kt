package com.group4.quizapp.ui.quiz

import com.group4.quizapp.domain.model.Question
import com.group4.quizapp.domain.model.QuizAttemptDetail
import com.group4.quizapp.domain.model.QuizResult
import com.group4.quizapp.domain.repository.QuizRepository
import com.group4.quizapp.domain.usecase.GetQuestionsUseCase
import com.group4.quizapp.domain.usecase.SaveQuizResultUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val testQuestions = listOf(
        Question(
            id = 1, questionText = "Q1", optionA = "A", optionB = "B",
            optionC = "C", optionD = "D", correctOption = "A",
            category = "Test", difficulty = "Easy"
        ),
        Question(
            id = 2, questionText = "Q2", optionA = "A", optionB = "B",
            optionC = "C", optionD = "D", correctOption = "B",
            category = "Test", difficulty = "Easy"
        )
    )

    private lateinit var viewModel: QuizViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val fakeRepository = FakeQuizRepository(testQuestions)
        viewModel = QuizViewModel(
            GetQuestionsUseCase(fakeRepository),
            SaveQuizResultUseCase(fakeRepository)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `correct answer increments score`() = runTest {
        viewModel.loadQuestions("Test", "Easy")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.answerQuestion("A")

        assertEquals(1, viewModel.uiState.value.score)
    }

    @Test
    fun `incorrect answer does not increment score`() = runTest {
        viewModel.loadQuestions("Test", "Easy")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.answerQuestion("B")

        assertEquals(0, viewModel.uiState.value.score)
    }

    @Test
    fun `currentIndex increments on answer`() = runTest {
        viewModel.loadQuestions("Test", "Easy")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.answerQuestion("A")

        assertEquals(1, viewModel.uiState.value.currentIndex)
    }

    @Test
    fun `quiz finishes after last question`() = runTest {
        viewModel.loadQuestions("Test", "Easy")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.answerQuestion("A")
        viewModel.answerQuestion("B")

        assertTrue(viewModel.uiState.value.isFinished)
    }
}

private class FakeQuizRepository(private val questions: List<Question>) : QuizRepository {
    override suspend fun getQuestions(category: String, difficulty: String): List<Question> = questions
    override suspend fun insertResult(result: QuizResult): Long = 1L
    override suspend fun insertAttemptDetails(details: List<QuizAttemptDetail>) {}
    override suspend fun getAttemptDetails(resultId: Int): List<QuizAttemptDetail> = emptyList()
    override fun getAllResults(): Flow<List<QuizResult>> = flowOf(emptyList())
    override fun searchResults(query: String): Flow<List<QuizResult>> = flowOf(emptyList())
    override fun getTopScoresByCategory(): Flow<List<QuizResult>> = flowOf(emptyList())
    override suspend fun clearResults() {}
    override suspend fun seedDatabase() {}
}
