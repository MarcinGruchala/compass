package com.example.compass.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.compass.databinding.ActivitySetDestinationBinding
import com.example.compass.viewmodels.MainActivityViewModel
import com.example.compass.viewmodels.SetDestinationActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetDestinationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetDestinationBinding
    private val viewModel: SetDestinationActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSubmit.setOnClickListener {
            //update repository with destination
        }

        binding.btnGoBack.setOnClickListener {
            finish()
        }
    }
}
