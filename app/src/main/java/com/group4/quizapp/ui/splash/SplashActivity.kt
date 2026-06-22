package com.group4.quizapp.ui.splash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.group4.quizapp.databinding.ActivitySplashBinding
import com.group4.quizapp.ui.base.BaseActivity
import com.group4.quizapp.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    private val handler = Handler(Looper.getMainLooper())
    private val navigateTask = Runnable {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun initViews() {
        handler.postDelayed(navigateTask, 2000)
    }

    override fun onDestroy() {
        handler.removeCallbacks(navigateTask)
        super.onDestroy()
    }
}
