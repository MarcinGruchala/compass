package com.example.compass.viewmodels

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.example.compass.models.GeoLocation
import com.example.compass.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    var lastAccelerometerValue = FloatArray(3)
    var lastMagneticFieldValue = FloatArray(3)
    var rotationMatrix = FloatArray(9)
    var orientation = FloatArray(3)

    var lastSensorsUpdateTime = 0L
    var currentCompassAzimuth = 0f

    var currentDestinationArrowAzimuth = 0f

    private val isLastAccelerometerValueCopied: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    private val isLastMagnetFiledValueCopied: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val currentLocation: MutableLiveData<Location> by lazy {
        MutableLiveData<Location>()
    }

    val isDestinationUpdated: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    private val destinationObserver = Observer<GeoLocation> {
        isDestinationUpdated.value = true
    }

    init {
        repository.destination.observeForever(destinationObserver)
    }

    fun getNewAzimuthInDegrees() = Math.toDegrees(orientation[0].toDouble()).toFloat()

    fun getDestination() = repository.destination.value

    fun checkDestination(): Boolean {
        if (repository.destination.value == null) {
            return false
        }
        return true
    }

    fun saveSensorEventData(
        event: SensorEvent,
        accelerometerSensor: Sensor,
        magneticFieldSensor: Sensor
    ) {
        if (event.sensor == accelerometerSensor ) {
            System.arraycopy(
                event.values,
                0,
                lastAccelerometerValue,
                0,event.values.size
            )
            isLastAccelerometerValueCopied.value = true
        } else if (event.sensor == magneticFieldSensor) {
            System.arraycopy(
                event.values,
                0,
                lastMagneticFieldValue,
                0,
                event.values.size
            )
            isLastMagnetFiledValueCopied.value = true
        }
    }

    fun validateNewSensorData(): Boolean {
        if (isLastAccelerometerValueCopied.value == true &&
            isLastMagnetFiledValueCopied.value == true &&
            System.currentTimeMillis() - lastSensorsUpdateTime > 250) {
            return true
        }
        return false
    }

    fun calculateOrientation() {
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            lastAccelerometerValue,
            lastMagneticFieldValue
        )
        SensorManager.getOrientation(
            rotationMatrix,
            orientation
        )
    }
}
