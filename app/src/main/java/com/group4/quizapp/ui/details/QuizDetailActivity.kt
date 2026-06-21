package com.group4.quizapp.ui.details

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

class QuizDetailActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val viewModel: QuizDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_details)

        // Handle window insets for safe header and footer
        val rootLayout = findViewById<android.view.ViewGroup>(R.id.detailsRoot)
        val header = findViewById<android.view.ViewGroup>(R.id.detailsHeader)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            header.updatePadding(top = systemBars.top)
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerDetails)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val resultId = intent.getIntExtra("resultId", -1)
        
        viewModel.details.observe(this) { details ->
            recyclerView.adapter = QuizDetailAdapter(details)
        }

        if (resultId != -1) {
            viewModel.loadDetails(resultId)
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}
