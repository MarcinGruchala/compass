package com.example.compass.repository

import androidx.lifecycle.MutableLiveData
import com.example.compass.models.GeoLocation

class Repository {

    val destination: MutableLiveData<GeoLocation> by lazy {
        MutableLiveData<GeoLocation>()
    }
}