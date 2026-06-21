package com.group4.quizapp.ui.history

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group4.quizapp.R
import com.group4.quizapp.ui.details.QuizDetailActivity
import com.group4.quizapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClearHistory: Button
    private lateinit var searchView: SearchView
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Handle window insets for safe header and footer
        val rootLayout = findViewById<android.view.ViewGroup>(R.id.historyRoot)
        val header = findViewById<android.view.ViewGroup>(R.id.historyHeader)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            header.updatePadding(top = systemBars.top)
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerHistory)
        btnClearHistory = findViewById(R.id.btnClearHistory)
        searchView = findViewById(R.id.searchHistory)

        recyclerView.layoutManager = LinearLayoutManager(this)

        setupObservers()
        setupSearch()

        btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        // Go Home button
        val btnGoHomeHistory = findViewById<Button>(R.id.btnGoHomeHistory)
        btnGoHomeHistory.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.results.collect { results ->
                    if (results == null) return@collect // still loading

                    if (results.isEmpty() && searchView.query.isNullOrBlank()) {
                        Toast.makeText(
                            this@HistoryActivity,
                            "No history found!",
                            Toast.LENGTH_SHORT
                        ).show()
                        recyclerView.adapter = HistoryAdapter(emptyList()) {}
                    } else {
                        recyclerView.adapter = HistoryAdapter(results) { result ->
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
