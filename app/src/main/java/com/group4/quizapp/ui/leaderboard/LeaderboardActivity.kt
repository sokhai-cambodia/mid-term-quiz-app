package com.group4.quizapp.ui.leaderboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group4.quizapp.R
import com.group4.quizapp.ui.main.MainActivity

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        recyclerView = findViewById(R.id.recyclerLeaderboard)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupObservers()
        viewModel.loadLeaderboard()

        findViewById<Button>(R.id.btnGoHomeLeaderboard).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        viewModel.topScores.observe(this) { scores ->
            recyclerView.adapter = LeaderboardAdapter(scores)
        }
    }
}
