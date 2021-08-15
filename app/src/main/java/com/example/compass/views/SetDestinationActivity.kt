package com.example.compass.views

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.compass.R
import com.example.compass.databinding.ActivitySetDestinationBinding
import com.example.compass.models.DestinationInputStatus
import com.example.compass.models.GeoLocation
import com.example.compass.utils.DestinationInputValidator
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
            Log.d("SetDestinationActivity", "Lat: ${binding.etLat.text} Lon: ${binding.etLon.text}")
            val inputValidation = DestinationInputValidator.validateDestinationInput(
                binding.etLat.text.toString(),
                binding.etLon.text.toString()
            )
            if (inputValidation == DestinationInputStatus.VALID){
                viewModel.updateRepositoryWithDestination(
                    GeoLocation(
                        binding.etLat.text.toString().toDouble(),
                        binding.etLon.text.toString().toDouble()
                    )
                )
                finish()
            } else {
                handleInputError(inputValidation)
            }
        }

        binding.btnGoBack.setOnClickListener {
            finish()
        }
    }

    private fun handleInputError(inputStatus: DestinationInputStatus) {
        when(inputStatus) {
            DestinationInputStatus.OUT_OF_RANGE -> {
                binding.tvSetDestinationDescription.text = getString(
                    R.string.destination_input_status_outOfRange_message
                )
                binding.tvSetDestinationDescription.setTextColor(
                    ContextCompat.getColor(this, R.color.red)
                )

            }
            DestinationInputStatus.EMPTY -> {
                binding.tvSetDestinationDescription.text = getString(
                    R.string.destination_input_status_empty_message
                )
                binding.tvSetDestinationDescription.setTextColor(
                    ContextCompat.getColor(this, R.color.red)
                )
            }
            else -> {}
        }

    }
}
