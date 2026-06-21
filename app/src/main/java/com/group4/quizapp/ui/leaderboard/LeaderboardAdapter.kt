package com.group4.quizapp.ui.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group4.quizapp.R
import com.group4.quizapp.data.database.QuizResult

class LeaderboardAdapter(private val results: List<QuizResult>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tvRank)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDifficulty: TextView = view.findViewById(R.id.tvDifficulty)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvScore: TextView = view.findViewById(R.id.tvScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val result = results[position]
        holder.tvRank.text = "#${position + 1}"
        holder.tvCategory.text = result.category
        holder.tvDifficulty.text = result.difficulty
        holder.tvDate.text = result.dateTaken
        holder.tvScore.text = result.score.toString()
    }

    override fun getItemCount() = results.size
}
