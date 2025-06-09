package at.appdev.exercise3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.appdev.exercise3.ui.theme.Assignment08Theme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private val _broadcastFlow = MutableStateFlow<String>("No broadcasts yet")
    val broadcastFlow = _broadcastFlow
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                var prefix = "INT"
                if (intent.action == "at.appdev.SEND_BC") {
                    prefix = "EXT"
                }
                _broadcastFlow.value = "$prefix Broadcast received at ${System.currentTimeMillis()}"
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter().apply {
            addAction("at.appdev.SEND_BC")
            addAction("at.appdev.SEND_BC_INTERNAL")
        }
        registerReceiver(receiver, filter, RECEIVER_EXPORTED)
        setContent {
            Assignment08Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var lastBroadcast by remember { mutableStateOf("No broadcasts yet") }
                    LaunchedEffect(Unit) {
                        broadcastFlow.collect { message ->
                            lastBroadcast = message
                        } }
                    Column(modifier = Modifier.padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)) {
                        Text("Broadcast Receiver Demo")
                        Button(onClick = { sendTestBroadcast() }) {
                            Text("Send Test Broadcast")
                        }
                        Spacer(modifier = Modifier.padding(20.dp))
                        Text("Status: $lastBroadcast")
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
    private fun sendTestBroadcast() {
        sendBroadcast(
            Intent("at.appdev.SEND_BC_INTERNAL").apply {
                setPackage(packageName) // Target current app
            }
        )
    }
}