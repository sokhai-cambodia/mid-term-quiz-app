package com.group4.quizapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.group4.quizapp.database.QuizDatabase
import com.group4.quizapp.database.QuizResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClearHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        recyclerView = findViewById(R.id.recyclerHistory)
        btnClearHistory = findViewById(R.id.btnClearHistory)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadHistory()

        btnClearHistory.setOnClickListener {
            clearHistory()
      }
// Go Home button
        val btnGoHomeHistory = findViewById<Button>(R.id.btnGoHomeHistory)
        btnGoHomeHistory.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
      }

 }

    private fun loadHistory() {
        val db = QuizDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            val results = db.quizDao().getAllResults()
            withContext(Dispatchers.Main) {
                if (results.isEmpty()) {
                    Toast.makeText(
                        this@HistoryActivity,
                        "No history yet — play a quiz first!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    recyclerView.adapter = HistoryAdapter(results)
                }
            }
        }
    }

    private fun clearHistory() {
        val db = QuizDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            db.quizDao().clearAllResults()
            withContext(Dispatchers.Main) {
                recyclerView.adapter = HistoryAdapter(emptyList())
                Toast.makeText(
                    this@HistoryActivity,
                    "History cleared!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

class HistoryAdapter(private val results: List<QuizResult>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDifficulty: TextView = view.findViewById(R.id.tvDifficulty)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTimeTaken: TextView = view.findViewById(R.id.tvTimeTaken)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
        val tvPercentage: TextView = view.findViewById(R.id.tvPercentage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)

    }
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val result = results[position]
        holder.tvCategory.text = result.category
        holder.tvDifficulty.text = result.difficulty
        holder.tvDate.text = result.dateTaken
        val minutes = result.timeSpent / 60
        val seconds = result.timeSpent % 60
        holder.tvTimeTaken.text = if (minutes > 0) "Time: ${minutes}m ${seconds}s" else "Time: ${seconds}s"
        holder.tvScore.text = "${result.score} / ${result.totalQuestions}"

        // Calculate and show percentage
        val percentage = if (result.totalQuestions > 0)
            (result.score * 100 / result.totalQuestions) else 0
        holder.tvPercentage.text = "$percentage%"

        // Color code the percentage
        holder.tvPercentage.setTextColor(
            when {
                percentage >= 80 -> 0xFF2ECC71.toInt() // Green
                percentage >= 60 -> 0xFFF5A623.toInt() // Orange
                percentage >= 40 -> 0xFFE67E22.toInt() // Dark orange
                else -> 0xFFE74C3C.toInt()             // Red
            }
        )
    }

    override fun getItemCount() = results.size
}