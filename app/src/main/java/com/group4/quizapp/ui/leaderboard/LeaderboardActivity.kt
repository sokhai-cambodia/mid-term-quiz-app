package com.group4.quizapp.ui.leaderboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group4.quizapp.R
import com.group4.quizapp.ui.main.MainActivity

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val viewModel: LeaderboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Handle window insets for safe header and footer
        val rootLayout = findViewById<android.view.ViewGroup>(R.id.leaderboardRoot)
        val header = findViewById<android.view.ViewGroup>(R.id.leaderboardHeader)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            header.updatePadding(top = systemBars.top)
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

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
