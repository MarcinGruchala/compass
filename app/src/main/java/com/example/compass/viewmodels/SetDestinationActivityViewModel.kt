package com.example.compass.viewmodels

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.example.compass.R
import com.example.compass.models.GeoLocation
import com.example.compass.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetDestinationActivityViewModel @Inject constructor(
    application: Application,
    private val repository: Repository
) : AndroidViewModel(application) {

    var messageText = application.getString(
        R.string.destination_input_description
    )

    var messageColor = ContextCompat.getColor(
        application.applicationContext,
        R.color.main_green)

    fun updateRepositoryWithDestination(newGeoLocation: GeoLocation) {
        repository.destination.value = newGeoLocation
    }
}