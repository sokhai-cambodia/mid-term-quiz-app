package com.group4.quizapp.ui.settings

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.group4.quizapp.R
import com.group4.quizapp.databinding.ActivitySettingsBinding
import com.group4.quizapp.ui.base.BaseActivity
import com.group4.quizapp.ui.main.MainActivity

class SettingsActivity : BaseActivity<ActivitySettingsBinding>(ActivitySettingsBinding::inflate) {

    private val viewModel: SettingsViewModel by viewModels()
    private var currentToast: Toast? = null

    override fun getHeaderView(): View = binding.settingsHeader

    override fun initViews() {
        binding.apply {
            // Load saved preferences
            switchDarkMode.isChecked = prefs.isDarkMode
            updateTimerSelectionUI()

            // Dark mode toggle
            switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                prefs.isDarkMode = isChecked
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            // Timer buttons
            btn15s.setOnClickListener {
                prefs.timerDuration = 15
                updateTimerSelectionUI()
                showToast("Timer set to 15 seconds")
            }
            btn30s.setOnClickListener {
                prefs.timerDuration = 30
                updateTimerSelectionUI()
                showToast("Timer set to 30 seconds")
            }
            btn60s.setOnClickListener {
                prefs.timerDuration = 60
                updateTimerSelectionUI()
                showToast("Timer set to 60 seconds")
            }
            btn90s.setOnClickListener {
                prefs.timerDuration = 90
                updateTimerSelectionUI()
                showToast("Timer set to 90 seconds")
            }

            // Clear all history
            btnClearAllHistory.setOnClickListener {
                viewModel.clearHistory()
                showToast("All history cleared!")
            }

            // Go Home button
            btnGoHomeSettings.setOnClickListener {
                val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
    }

    private fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }

    private fun updateTimerSelectionUI() {
        val selectedDuration = prefs.timerDuration
        
        val activeColor = 0xFF1B3A6B.toInt()
        val activeTextColor = 0xFFFFFFFF.toInt()
        
        val inactiveColor = ContextCompat.getColor(this, R.color.cardBackgroundColor)
        val inactiveTextColor = ContextCompat.getColor(this, R.color.primaryTextColor)

        binding.apply {
            val buttons = listOf(btn15s, btn30s, btn60s, btn90s)
            val durations = listOf(15, 30, 60, 90)

            for (i in buttons.indices) {
                if (durations[i] == selectedDuration) {
                    buttons[i].setBackgroundColor(activeColor)
                    buttons[i].setTextColor(activeTextColor)
                    buttons[i].alpha = 1.0f
                } else {
                    buttons[i].setBackgroundColor(inactiveColor)
                    buttons[i].setTextColor(inactiveTextColor)
                    buttons[i].alpha = 0.8f
                }
            }
        }
    }

    override fun onDestroy() {
        currentToast?.cancel()
        super.onDestroy()
    }
}
