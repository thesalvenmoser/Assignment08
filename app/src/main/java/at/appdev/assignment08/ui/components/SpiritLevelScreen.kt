package at.appdev.assignment08.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.appdev.assignment08.model.TiltState

@Composable
fun SpiritLevelScreen(tiltState: TiltState) {
    val bubbleRadius = 40f
    val maxOffset = 200f // clamp range

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tilt X: %.1f°".format(tiltState.xAngle))
            Text("Tilt Y: %.1f°".format(tiltState.yAngle))

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.Yellow, shape = CircleShape)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2

                    val offsetX = (-tiltState.xAngle / 90f).coerceIn(-1f, 1f) * maxOffset
                    val offsetY = (tiltState.yAngle / 90f).coerceIn(-1f, 1f) * maxOffset

                    drawCircle(
                        color = Color.Blue,
                        radius = bubbleRadius,
                        center = Offset(centerX + offsetX, centerY + offsetY)
                    )
                }
            }
        }
    }
}
