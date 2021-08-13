package com.example.compass

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.compass.databinding.ActivityMainBinding

private const val FINE_LOCATION_PERMISSION_REQUEST_CODE = 10
class MainActivity : AppCompatActivity(), SensorEventListener  {
    private val requiredPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometerSensor: Sensor
    private lateinit var magneticFieldSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPermissions()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onResume() {
        super.onResume()
        registerSensorsListeners()
    }

    override fun onPause() {
        super.onPause()
        unRegisterSensorsListeners()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            saveSensorEventData(event)
            if (validateNewSensorData()) {
                calculateOrientation()
                updateCompass(viewModel.getNewAzimuthInDegrees())
                updateDestinationArrow(-viewModel.getNewAzimuthInDegrees())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            getPermissions()
        }
    }

    private fun getPermissions() {
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                FINE_LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermissions() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
    }

    private fun registerSensorsListeners() {
        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this,magneticFieldSensor,SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun unRegisterSensorsListeners() {
        sensorManager.unregisterListener(this, accelerometerSensor)
        sensorManager.unregisterListener(this, magneticFieldSensor)
    }

    private fun saveSensorEventData(event: SensorEvent) {
        if (event.sensor == accelerometerSensor ) {
            System.arraycopy(
                event.values,
                0,
                viewModel.lastAccelerometerValue,
                0,event.values.size
            )
            viewModel.isLastAccelerometerValueCopied.value = true
        } else if (event.sensor == magneticFieldSensor) {
            System.arraycopy(
                event.values,
                0,
                viewModel.lastMagneticFieldValue,
                0,
                event.values.size
            )
            viewModel.isLastMagnetFiledValueCopied.value = true
        }
    }

    private fun validateNewSensorData(): Boolean {
        if (viewModel.isLastAccelerometerValueCopied.value == true &&
            viewModel.isLastMagnetFiledValueCopied.value == true &&
            System.currentTimeMillis() - viewModel.lastSensorsUpdateTime > 250) {
            return true
        }
        return false
    }

    private fun calculateOrientation() {
        SensorManager.getRotationMatrix(
            viewModel.rotationMatrix,
            null,
            viewModel.lastAccelerometerValue,
            viewModel.lastMagneticFieldValue
        )
        SensorManager.getOrientation(
            viewModel.rotationMatrix,
            viewModel.orientation
        )
    }

    private fun updateCompass(newAzimuth: Float) {
        val rotationAnimation = RotateAnimation(
            viewModel.currentCompassAzimuth,
            -newAzimuth,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        rotationAnimation.duration = 250
        rotationAnimation.fillAfter = true

        binding.ivCompass.startAnimation(rotationAnimation)

        viewModel.currentCompassAzimuth = -newAzimuth
        viewModel.lastSensorsUpdateTime = System.currentTimeMillis()
    }

    private fun updateDestinationArrow(newAzimuth: Float) {
        val rotationAnimation = RotateAnimation(
            viewModel.currentDestinationArrowAzimuth,
            -newAzimuth,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        rotationAnimation.duration = 250
        rotationAnimation.fillAfter = true

        binding.ivDestinationArrow.startAnimation(rotationAnimation)

        viewModel.currentDestinationArrowAzimuth = -newAzimuth
    }
}
