package at.appdev.assignment08

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import at.appdev.assignment08.model.TiltState
import at.appdev.assignment08.ui.components.SpiritLevelScreen
import kotlin.math.atan2
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accSensor: Sensor? = null

    private val _tiltState = mutableStateOf(TiltState(0f, 0f))
    val tiltState: State<TiltState> = _tiltState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            SpiritLevelScreen(tiltState = tiltState.value)
        }
    }
    override fun onResume() {
        super.onResume()
        accSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
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
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val xAngle = Math.toDegrees(atan2(x.toDouble(), sqrt(y*y + z*z).toDouble())).toFloat()
            val yAngle = Math.toDegrees(atan2(y.toDouble(), sqrt(x*x + z*z).toDouble())).toFloat()

            _tiltState.value = TiltState(xAngle, yAngle)
        }    }
}