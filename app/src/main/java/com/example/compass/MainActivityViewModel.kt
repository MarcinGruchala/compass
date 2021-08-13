package com.example.compass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compass.model.GeoLocation

class MainActivityViewModel : ViewModel() {
    var lastAccelerometerValue = FloatArray(3)
    var lastMagneticFieldValue = FloatArray(3)
    var rotationMatrix = FloatArray(9)
    var orientation = FloatArray(3)

    var lastSensorsUpdateTime = 0L
    var currentCompassAzimuth = 0f

    var currentDestinationArrowAzimuth = 0f

    val isLastAccelerometerValueCopied: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val isLastMagnetFiledValueCopied: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val currentLocation: MutableLiveData<GeoLocation> by lazy {
        MutableLiveData<GeoLocation>()
    }

    fun getNewAzimuthInDegrees() = Math.toDegrees(orientation[0].toDouble()).toFloat()
}