package com.group4.quizapp.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group4.quizapp.R
import com.group4.quizapp.domain.model.QuizResult

class HistoryAdapter(
    private val results: List<QuizResult>,
    private val onItemClick: (QuizResult) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

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

        val percentage = if (result.totalQuestions > 0)
            (result.score * 100 / result.totalQuestions) else 0
        holder.tvPercentage.text = "$percentage%"

        holder.tvPercentage.setTextColor(
            when {
                percentage >= 80 -> 0xFF2ECC71.toInt()
                percentage >= 60 -> 0xFFF5A623.toInt()
                percentage >= 40 -> 0xFFE67E22.toInt()
                else -> 0xFFE74C3C.toInt()
            }
        )

        holder.itemView.setOnClickListener {
            onItemClick(result)
        }
    }

    override fun getItemCount() = results.size
}
