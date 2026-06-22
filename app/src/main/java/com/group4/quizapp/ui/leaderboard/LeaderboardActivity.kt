package com.group4.quizapp.ui.leaderboard

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.group4.quizapp.databinding.ActivityLeaderboardBinding
import com.group4.quizapp.ui.base.BaseActivity
import com.group4.quizapp.ui.details.QuizDetailActivity
import com.group4.quizapp.ui.main.MainActivity
import kotlinx.coroutines.launch

class LeaderboardActivity : BaseActivity<ActivityLeaderboardBinding>(ActivityLeaderboardBinding::inflate) {

    private val viewModel: LeaderboardViewModel by viewModels()

    override fun getHeaderView(): View = binding.leaderboardHeader

    override fun initViews() {
        binding.recyclerLeaderboard.layoutManager = LinearLayoutManager(this)

        binding.btnGoHomeLeaderboard.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.topScores.collect { scores ->
                    binding.recyclerLeaderboard.adapter = LeaderboardAdapter(scores) { result ->
                        val intent = Intent(this@LeaderboardActivity, QuizDetailActivity::class.java)
                        intent.putExtra("resultId", result.id)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}
