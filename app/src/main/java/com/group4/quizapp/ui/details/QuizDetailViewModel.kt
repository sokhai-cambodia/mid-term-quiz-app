package com.group4.quizapp.ui.details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.data.database.QuizAttemptDetail
import com.group4.quizapp.data.database.QuizDatabase
import com.group4.quizapp.data.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository(
        QuizDatabase.getDatabase(application).quizDao()
    )

    private val _details = MutableLiveData<List<QuizAttemptDetail>>()
    val details: LiveData<List<QuizAttemptDetail>> = _details

    fun loadDetails(resultId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getAttemptDetails(resultId)
            _details.postValue(data)
        }
    }
}
