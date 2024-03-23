import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun DraggableBubbleScreen() {
    var bubbles by remember { mutableStateOf(listOf(0.1f, -0.05f)) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        bubbles.forEach { percentageChange ->
            DraggableBubble(
                size = 50.dp,
                percentageChange = percentageChange,
                onDrag = { newPosition ->
                    // Handle drag if needed
                }
            )
        }
    }
}

@Composable
fun DraggableBubble(
    size: Dp,
    percentageChange: Float,
    onDrag: (Offset) -> Unit
) {
    val color = if (percentageChange >= 0) Color.Green else Color.Red
    val adjustedSize = size + (percentageChange * 100).dp // Adjust size based on percentage change

    var position by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    position.x.roundToInt(),
                    position.y.roundToInt()
                )
            }
            .background(color, shape = CircleShape)
            .size(adjustedSize)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    position += dragAmount
                    onDrag(position)
                }
            }
    )
}
