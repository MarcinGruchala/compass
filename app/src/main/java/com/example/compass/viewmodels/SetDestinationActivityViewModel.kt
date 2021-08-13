package com.example.compass.viewmodels

import androidx.lifecycle.ViewModel
import com.example.compass.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetDestinationActivityViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    fun updateRepositoryWithDestination() {

    }


}