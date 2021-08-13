package com.example.compass

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.compass.databinding.ActivityMainBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task

private const val FINE_LOCATION_PERMISSION_REQUEST_CODE = 10
private const val REQUEST_CHECK_SETTINGS = 20
class MainActivity : AppCompatActivity(), SensorEventListener  {
    private val requiredPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometerSensor: Sensor
    private lateinit var magneticFieldSensor: Sensor
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPermissions()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d("MainActivity", "Current location: $location")
                    viewModel.currentLocation.value = location
                }
            }
        }

        val currentLocationObserver = Observer<Location> { location ->
            binding.tvLocation.text = "Lat: ${location.latitude} Lon: ${location.longitude}"
        }
        viewModel.currentLocation.observe(this, currentLocationObserver)
    }


    override fun onResume() {
        super.onResume()
        registerSensorsListeners()
        checkLocationSettingsAndStartLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        unRegisterSensorsListeners()
        stopLocationUpdates()
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

    private fun checkPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkLocationSettingsAndStartLocationUpdates() {
        val locationBuilder = LocationSettingsRequest.Builder()
            .addAllLocationRequests(listOf(locationRequest))

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(locationBuilder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
           startLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkPermissions()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            getPermissions()
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
