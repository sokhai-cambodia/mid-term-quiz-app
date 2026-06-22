package com.group4.quizapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.group4.quizapp.data.model.Question
import com.group4.quizapp.data.model.QuizResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizDaoTest {

    private lateinit var database: QuizDatabase
    private lateinit var dao: QuizDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            QuizDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.quizDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetQuestions() = runBlocking {
        val question = Question(
            questionText = "Test Question",
            optionA = "A", optionB = "B", optionC = "C", optionD = "D",
            correctOption = "A", category = "Test", difficulty = "Easy"
        )
        dao.insertQuestion(question)
        val questions = dao.getQuestions("Test", "Easy")
        assertEquals(1, questions.size)
        assertEquals("Test Question", questions[0].questionText)
    }

    @Test
    fun insertAndGetResults() = runBlocking {
        val result = QuizResult(
            category = "Test", difficulty = "Easy", score = 5,
            totalQuestions = 5, dateTaken = "21 Jun 2026", timeSpent = 30
        )
        dao.insertResult(result)
        val results = dao.getAllResults().first()
        assertEquals(1, results.size)
        assertEquals(5, results[0].score)
    }

    @Test
    fun clearResults() = runBlocking {
        dao.insertResult(QuizResult(category = "T1", score = 5, totalQuestions = 5, dateTaken = "D1"))
        dao.clearResults()
        val results = dao.getAllResults().first()
        assertEquals(0, results.size)
    }

    @Test
    fun searchResults() = runBlocking {
        dao.insertResult(QuizResult(category = "Science", score = 5, totalQuestions = 5, dateTaken = "D1"))
        dao.insertResult(QuizResult(category = "Math", score = 3, totalQuestions = 5, dateTaken = "D2"))

        val searchSci = dao.searchResults("Sci").first()
        assertEquals(1, searchSci.size)
        assertEquals("Science", searchSci[0].category)
    }
}
