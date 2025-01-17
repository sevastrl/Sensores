package com.example.gps2
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gps2.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvGravimeter: TextView
    private lateinit var btnGetLocation: Button

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLatitude = findViewById(R.id.tv_latitude)
        tvLongitude = findViewById(R.id.tv_longitude)
        tvGravimeter = findViewById(R.id.tv_gravimeter)
        btnGetLocation = findViewById(R.id.btn_get_location)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        btnGetLocation.setOnClickListener {
            checkLocationPermission()
        }
    }
    private fun checkLocationPermission() {
        // Verifica si el permiso está otorgado
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Si el permiso está otorgado, obtiene la ubicación
            getLocation()
        } else {
            // Si no está otorgado, solicita el permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                tvLatitude.text = "Latitud: ${location.latitude}"
                tvLongitude.text = "Longitud: ${location.longitude}"
            } else {
                tvLatitude.text = "Latitud: No disponible"
                tvLongitude.text = "Longitud: No disponible"
            }
        }.addOnFailureListener {
            tvLatitude.text = "Error al obtener ubicación"
            tvLongitude.text = "Reintente más tarde"
        }
    }

    override fun onResume() {
        super.onResume()
        // Registra el listener para el acelerómetro
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Detiene el listener para ahorrar batería
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            tvGravimeter.text = "Gravímetro (X, Y, Z):\nX: $x\nY: $y\nZ: $z"
        }
    }

}
