[Problems]
- Android provides a BroadcastReceiver mechanism to respond to system-wide broadcast events — such as changes to the battery level. This exercise introduces you to the concept and use of dynamic broadcast receivers, aligned with modern Android development practices. Your task is to build a simple app that listens for battery level changes using a dynamically registered BroadcastReceiver.
  - Create a custom BroadcastReceiver class that:
    - Listens for Intent.ACTION_BATTERY_CHANGED
    - Extracts the battery level from the incoming Intent
    - Logs the battery level (use Log.d ) and updates UI state accordingly
  - Dynamically register the receiver from your main activity or composable
  - Unregister the receiver when the activity/composable leaves the foreground (hint: use DisposableEffect)
  - For testing, use the Extended Controls panel in the Android Emulator to simulate battery level changes
[Examples]
- AndroidManifest
``` xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <permission
        android:name="at.appdev.BROADCAST_PERMISSION"
        android:protectionLevel="normal" />
    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Assignment08"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.Assignment08">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```
- MainActivity.java with Broadcast Sender
``` kotlin
package at.appdev.assignment08

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.appdev.assignment08.ui.theme.Assignment08Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assignment08Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SendGUI(
                        title = "Send Broadcast",
                        sendMethod = ::sendCrossAppBroadcast,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
    fun sendCrossAppBroadcast() {
        val intent = Intent().apply {
            action = "at.appdev.SEND_BC"
            //putExtra("message", "Message in a bottle!")
            // required since Android 8
            setPackage("com.example.broadcastreceiver") // target app package name
            flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        }
        // send with permission
        sendBroadcast(intent, "at.appdev.BROADCAST_PERMISSION")
    }
}
@Composable
fun SendGUI(title: String, sendMethod: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = sendMethod, modifier = modifier) {
        Text(title)
    }
}
```
- MainActivity.kt with Broadcast Receiver
``` kotlin
package at.appdev.assignment08

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
import at.appdev.assignment08.ui.theme.Assignment08Theme
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
```