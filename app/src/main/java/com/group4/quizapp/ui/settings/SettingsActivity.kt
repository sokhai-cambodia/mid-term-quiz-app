package com.group4.quizapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.group4.quizapp.R
import com.group4.quizapp.ui.main.MainActivity

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()
    
    private lateinit var btn15s: Button
    private lateinit var btn30s: Button
    private lateinit var btn60s: Button
    private lateinit var btn90s: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Handle window insets for safe header and footer
        val rootLayout = findViewById<android.view.ViewGroup>(R.id.settingsRoot)
        val header = findViewById<android.view.ViewGroup>(R.id.settingsHeader)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            header.updatePadding(top = systemBars.top)
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }

        val switchDarkMode = findViewById<SwitchCompat>(R.id.switchDarkMode)
        btn15s = findViewById(R.id.btn15s)
        btn30s = findViewById(R.id.btn30s)
        btn60s = findViewById(R.id.btn60s)
        btn90s = findViewById(R.id.btn90s)
        val btnClearAllHistory = findViewById<Button>(R.id.btnClearAllHistory)

        // Load saved preferences
        switchDarkMode.isChecked = viewModel.isDarkMode
        updateTimerSelectionUI()

        // Dark mode toggle
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            // Recreate activity to apply theme change immediately if needed, 
            // though setDefaultNightMode usually handles it.
        }

        // Timer buttons
        btn15s.setOnClickListener {
            viewModel.setTimerDuration(15)
            updateTimerSelectionUI()
            Toast.makeText(this, "Timer set to 15 seconds", Toast.LENGTH_SHORT).show()
        }
        btn30s.setOnClickListener {
            viewModel.setTimerDuration(30)
            updateTimerSelectionUI()
            Toast.makeText(this, "Timer set to 30 seconds", Toast.LENGTH_SHORT).show()
        }
        btn60s.setOnClickListener {
            viewModel.setTimerDuration(60)
            updateTimerSelectionUI()
            Toast.makeText(this, "Timer set to 60 seconds", Toast.LENGTH_SHORT).show()
        }
        btn90s.setOnClickListener {
            viewModel.setTimerDuration(90)
            updateTimerSelectionUI()
            Toast.makeText(this, "Timer set to 90 seconds", Toast.LENGTH_SHORT).show()
        }

        // Clear all history
        btnClearAllHistory.setOnClickListener {
            viewModel.clearHistory()
            Toast.makeText(this, "All history cleared!", Toast.LENGTH_SHORT).show()
        }

        // Go Home button
        val btnGoHomeSettings = findViewById<Button>(R.id.btnGoHomeSettings)
        btnGoHomeSettings.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun updateTimerSelectionUI() {
        val selectedDuration = viewModel.timerDuration
        
        val activeColor = 0xFF1B3A6B.toInt() // Dark Blue
        val activeTextColor = 0xFFFFFFFF.toInt() // White
        
        val inactive15Color = 0xFF1B3A6B.toInt()
        val inactive30Color = 0xFFF5A623.toInt()
        val inactive60Color = 0xFF30B0C7.toInt()
        val inactive90Color = 0xFFAAC4EE.toInt()
        val inactiveTextColor = 0xFF000000.toInt()

        // Reset colors to default (using what was in XML or similar)
        // Since buttons have backgroundTint in XML, we'll override here
        
        btn15s.setBackgroundColor(if (selectedDuration == 15) activeColor else inactive15Color)
        btn15s.setTextColor(if (selectedDuration == 15) activeTextColor else 0xFFFFFFFF.toInt()) // 15s was blue in XML
        if (selectedDuration != 15) btn15s.alpha = 0.5f else btn15s.alpha = 1.0f

        btn30s.setBackgroundColor(if (selectedDuration == 30) activeColor else inactive30Color)
        btn30s.setTextColor(if (selectedDuration == 30) activeTextColor else inactiveTextColor)
        if (selectedDuration != 30) btn30s.alpha = 0.5f else btn30s.alpha = 1.0f

        btn60s.setBackgroundColor(if (selectedDuration == 60) activeColor else inactive60Color)
        btn60s.setTextColor(if (selectedDuration == 60) activeTextColor else inactiveTextColor)
        if (selectedDuration != 60) btn60s.alpha = 0.5f else btn60s.alpha = 1.0f

        btn90s.setBackgroundColor(if (selectedDuration == 90) activeColor else inactive90Color)
        btn90s.setTextColor(if (selectedDuration == 90) activeTextColor else inactiveTextColor)
        if (selectedDuration != 90) btn90s.alpha = 0.5f else btn90s.alpha = 1.0f
    }
}
