package com.example.compass

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    var lastAccelerometerValue = FloatArray(3)
    var lastMagneticFieldValue = FloatArray(3)
    var rotationMatrix = FloatArray(9)
    var orientation = FloatArray(3)
    var lastSensorsUpdateTime = 0L
    var currentAzimuth = 0f

    val isLastAccelerometerValueCopied: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val isLastMagnetFiledValueCopied: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    fun getNewAzimuthInDegrees() = Math.toDegrees(orientation[0].toDouble()).toFloat()
}