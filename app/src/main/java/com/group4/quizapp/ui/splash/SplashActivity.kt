package com.group4.quizapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.group4.quizapp.R
import com.group4.quizapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val navigateToMain = Runnable {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Dark mode preference is applied once in QuizApplication.onCreate(),
        // before any Activity exists — no need to re-apply it here.

        // Wait 2 seconds then go to Home Screen
        handler.postDelayed(navigateToMain, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(navigateToMain)
    }
}
