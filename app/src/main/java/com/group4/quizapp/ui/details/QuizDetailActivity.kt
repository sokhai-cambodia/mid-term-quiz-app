package com.group4.quizapp.ui.details

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.group4.quizapp.databinding.ActivityQuizDetailsBinding
import com.group4.quizapp.ui.base.BaseActivity
import kotlinx.coroutines.launch

class QuizDetailActivity : BaseActivity<ActivityQuizDetailsBinding>(ActivityQuizDetailsBinding::inflate) {

    private val viewModel: QuizDetailViewModel by viewModels()

    override fun getHeaderView(): View = binding.detailsHeader

    override fun initViews() {
        binding.recyclerDetails.layoutManager = LinearLayoutManager(this)

        val resultId = intent.getIntExtra("resultId", -1)
        if (resultId != -1) {
            viewModel.loadDetails(resultId)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.details.collect { details ->
                    binding.recyclerDetails.adapter = QuizDetailAdapter(details)
                }
            }
        }
    }
}
