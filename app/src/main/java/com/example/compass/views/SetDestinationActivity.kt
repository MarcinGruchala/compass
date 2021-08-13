package com.example.compass.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.compass.databinding.ActivitySetDestinationBinding

class SetDestinationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetDestinationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnGoBack.setOnClickListener {
            finish()
        }
    }
}
