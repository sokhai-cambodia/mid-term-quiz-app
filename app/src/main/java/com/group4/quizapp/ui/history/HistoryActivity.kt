package com.group4.quizapp.ui.history

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.group4.quizapp.databinding.ActivityHistoryBinding
import com.group4.quizapp.ui.base.BaseActivity
import com.group4.quizapp.ui.details.QuizDetailActivity
import com.group4.quizapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryActivity : BaseActivity<ActivityHistoryBinding>(ActivityHistoryBinding::inflate) {

    private val viewModel: HistoryViewModel by viewModels()

    override fun getHeaderView(): View = binding.historyHeader

    override fun initViews() {
        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)

        binding.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.btnGoHomeHistory.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        setupSearch()
    }

    private fun setupSearch() {
        binding.searchHistory.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText.orEmpty())
                return true
            }
        })
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.results.collect { results ->
                    if (results == null) return@collect

                    if (results.isEmpty() && binding.searchHistory.query.isNullOrBlank()) {
                        Toast.makeText(this@HistoryActivity, "No history found!", Toast.LENGTH_SHORT).show()
                        binding.recyclerHistory.adapter = HistoryAdapter(emptyList()) {}
                    } else {
                        binding.recyclerHistory.adapter = HistoryAdapter(results) { result ->
                            val intent = Intent(this@HistoryActivity, QuizDetailActivity::class.java)
                            intent.putExtra("resultId", result.id)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}
