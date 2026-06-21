package com.group4.quizapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group4.quizapp.domain.usecase.SeedDatabaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val seedDatabaseUseCase: SeedDatabaseUseCase
) : ViewModel() {

    fun seedDatabase() = viewModelScope.launch {
        seedDatabaseUseCase()
    }
}
