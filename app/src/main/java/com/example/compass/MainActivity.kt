package com.example.compass

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.activity.viewModels
import androidx.core.graphics.rotationMatrix
import com.example.compass.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener  {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometerSensor: Sensor
    private lateinit var magneticFieldSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            if (viewModel.isLastAccelerometerValueCopied.value == true &&
                viewModel.isLastMagnetFiledValueCopied.value == true &&
                System.currentTimeMillis() - viewModel.lastSensorsUpdateTime > 250) {
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
                val azimuth = Math.toDegrees(viewModel.orientation[0].toDouble()).toFloat()
                updateCompass(azimuth)

            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun registerSensorsListeners() {
        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this,magneticFieldSensor,SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun unRegisterSensorsListeners() {
        sensorManager.unregisterListener(this, accelerometerSensor)
        sensorManager.unregisterListener(this, magneticFieldSensor)
    }


    private fun updateCompass(newAzimuth: Float) {
        val rotationAnimation = RotateAnimation(
            viewModel.currentAzimuth,
            -newAzimuth,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        rotationAnimation.duration = 250
        rotationAnimation.fillAfter = true

        binding.ivCompass.startAnimation(rotationAnimation)

        viewModel.currentAzimuth = -newAzimuth
        viewModel.lastSensorsUpdateTime = System.currentTimeMillis()
    }
}
