package com.group4.quizapp.di

import android.content.Context
import androidx.room.Room
import com.group4.quizapp.data.local.QuizDao
import com.group4.quizapp.data.local.QuizDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideQuizDatabase(@ApplicationContext context: Context): QuizDatabase =
        Room.databaseBuilder(
            context,
            QuizDatabase::class.java,
            "quiz_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideQuizDao(database: QuizDatabase): QuizDao = database.quizDao()
}
