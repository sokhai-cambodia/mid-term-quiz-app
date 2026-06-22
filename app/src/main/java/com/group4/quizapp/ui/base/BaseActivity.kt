package com.group4.quizapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewbinding.ViewBinding
import com.group4.quizapp.utils.PreferencesManager

abstract class BaseActivity<VB : ViewBinding>(
    private val inflate: (LayoutInflater) -> VB
) : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected val prefs: PreferencesManager by lazy { PreferencesManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        _binding = inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        initViews()
        setupObservers()
    }

    private fun setupInsets() {
        val headerView = getHeaderView()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            headerView?.updatePadding(top = systemBars.top)
            v.updatePadding(bottom = systemBars.bottom)
            insets
        }
    }

    protected open fun getHeaderView(): View? = null

    abstract fun initViews()
    
    open fun setupObservers() {}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
