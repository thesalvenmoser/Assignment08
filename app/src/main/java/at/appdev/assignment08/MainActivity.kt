package at.appdev.assignment08

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity(), SensorEventListener {
    lateinit var sensorManager: SensorManager
    var accSensor: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }
    override fun onResume() {
        super.onResume()
        accSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.i("Sensors", "Accuracy of sensor ${sensor?.name} changed to $accuracy")
    }
    override fun onSensorChanged(event: SensorEvent?) {
        Log.i("Sensors", "sensor data: ${event?.values?.first()}")
    }
}