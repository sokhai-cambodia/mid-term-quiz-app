package com.group4.quizapp.ui.quiz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuizViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: QuizViewModel

    @Before
    fun setup() {
        viewModel = QuizViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun `answerQuestion true increments score`() {
        viewModel.answerQuestion(true)
        assertEquals(1, viewModel.score.value)
    }

    @Test
    fun `answerQuestion false does not increment score`() {
        viewModel.answerQuestion(false)
        assertEquals(0, viewModel.score.value)
    }

    @Test
    fun `currentIndex increments on answer`() {
        viewModel.answerQuestion(true)
        assertEquals(1, viewModel.currentIndex.value)
    }
}
