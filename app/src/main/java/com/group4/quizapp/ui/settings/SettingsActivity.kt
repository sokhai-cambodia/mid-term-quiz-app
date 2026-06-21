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
        val btn15s = findViewById<Button>(R.id.btn15s)
        val btn30s = findViewById<Button>(R.id.btn30s)
        val btn60s = findViewById<Button>(R.id.btn60s)
        val btn90s = findViewById<Button>(R.id.btn90s)
        val btnClearAllHistory = findViewById<Button>(R.id.btnClearAllHistory)

        // Load saved preferences
        switchDarkMode.isChecked = viewModel.isDarkMode

        // Dark mode toggle
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Timer buttons
        btn15s.setOnClickListener {
            viewModel.setTimerDuration(15)
            Toast.makeText(this, "Timer set to 15 seconds", Toast.LENGTH_SHORT).show()
        }
        btn30s.setOnClickListener {
            viewModel.setTimerDuration(30)
            Toast.makeText(this, "Timer set to 30 seconds", Toast.LENGTH_SHORT).show()
        }
        btn60s.setOnClickListener {
            viewModel.setTimerDuration(60)
            Toast.makeText(this, "Timer set to 60 seconds", Toast.LENGTH_SHORT).show()
        }
        btn90s.setOnClickListener {
            viewModel.setTimerDuration(90)
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
}