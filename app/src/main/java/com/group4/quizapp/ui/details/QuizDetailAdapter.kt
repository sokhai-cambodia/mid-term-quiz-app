package com.group4.quizapp.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.group4.quizapp.R
import com.group4.quizapp.data.model.QuizAttemptDetail

class QuizDetailAdapter(private val details: List<QuizAttemptDetail>) :
    RecyclerView.Adapter<QuizDetailAdapter.DetailViewHolder>() {

    class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuestion: TextView = view.findViewById(R.id.tvQuestionText)
        val tvOptionA: TextView = view.findViewById(R.id.tvOptionA)
        val tvOptionB: TextView = view.findViewById(R.id.tvOptionB)
        val tvOptionC: TextView = view.findViewById(R.id.tvOptionC)
        val tvOptionD: TextView = view.findViewById(R.id.tvOptionD)
        val tvStatus: TextView = view.findViewById(R.id.tvResultStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_detail, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = details[position]
        holder.tvQuestion.text = item.questionText
        holder.tvOptionA.text = "A. ${item.optionA}"
        holder.tvOptionB.text = "B. ${item.optionB}"
        holder.tvOptionC.text = "C. ${item.optionC}"
        holder.tvOptionD.text = "D. ${item.optionD}"

        val options = listOf(holder.tvOptionA, holder.tvOptionB, holder.tvOptionC, holder.tvOptionD)
        val optionKeys = listOf("A", "B", "C", "D")

        // Reset styles
        val primaryText = ContextCompat.getColor(holder.itemView.context, R.color.primaryTextColor)
        options.forEach { it.setTextColor(primaryText); it.setTypeface(null, android.graphics.Typeface.NORMAL) }

        // Highlight correct option in green
        val correctIndex = optionKeys.indexOf(item.correctOption)
        if (correctIndex != -1) {
            options[correctIndex].setTextColor(0xFF2ECC71.toInt())
            options[correctIndex].setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // If wrong, highlight selected option in red
        if (item.selectedOption != item.correctOption && item.selectedOption != "None") {
            val selectedIndex = optionKeys.indexOf(item.selectedOption)
            if (selectedIndex != -1) {
                options[selectedIndex].setTextColor(0xFFE74C3C.toInt())
                options[selectedIndex].setTypeface(null, android.graphics.Typeface.BOLD)
            }
            holder.tvStatus.text = "Result: Incorrect"
            holder.tvStatus.setTextColor(0xFFE74C3C.toInt())
        } else if (item.selectedOption == "None") {
            holder.tvStatus.text = "Result: Timed Out"
            holder.tvStatus.setTextColor(0xFFF5A623.toInt())
        } else {
            holder.tvStatus.text = "Result: Correct"
            holder.tvStatus.setTextColor(0xFF2ECC71.toInt())
        }
    }

    override fun getItemCount() = details.size
}
