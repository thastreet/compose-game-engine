
import Animation.IDLE
import Animation.WALKING
import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

enum class Animation {
    IDLE,
    WALKING
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val movementDuration = 256
    val movementDistance = 16.dp
    val animationDuration = 256

    val pressed = remember { mutableSetOf<Key>() }
    var direction = remember { DOWN }
    var animation = remember { IDLE }

    val directionKeys = remember { setOf(Key.DirectionLeft, Key.DirectionRight, Key.DirectionUp, Key.DirectionDown) }

    Window(
        onCloseRequest = ::exitApplication,
        onPreviewKeyEvent = {
            when {
                (it.type == KeyEventType.KeyDown) -> {
                    pressed.add(it.key)
                    true
                }

                (it.type == KeyEventType.KeyUp) -> {
                    pressed.remove(it.key)
                    true
                }

                else -> false
            }
        }
    ) {
        MaterialTheme {
            Surface {
                var prevTime = remember { 0L }
                var totalMs = remember { movementDuration.toFloat() }
                var animationMs = remember { animationDuration.toFloat() }
                var x by remember { mutableStateOf(0.dp) }
                var y by remember { mutableStateOf(0.dp) }
                var frame by remember { mutableStateOf(0) }

                LaunchedEffect(Unit) {
                    while (true) {
                        withFrameNanos { time ->
                            val deltaMs = (time - prevTime) / (1000f * 1000f)
                            totalMs += deltaMs
                            animationMs += deltaMs
                            prevTime = time

                            val directionPressed = pressed.intersect(directionKeys).isNotEmpty()

                            if (directionPressed) {
                                if (totalMs >= movementDuration) {
                                    if (pressed.contains(Key.DirectionLeft)) {
                                        x -= movementDistance
                                        direction = LEFT
                                    } else if (pressed.contains(Key.DirectionRight)) {
                                        x += movementDistance
                                        direction = RIGHT
                                    }
                                    if (pressed.contains(Key.DirectionUp)) {
                                        y -= movementDistance
                                        direction = UP
                                    } else if (pressed.contains(Key.DirectionDown)) {
                                        y += movementDistance
                                        direction = DOWN
                                    }
                                    totalMs = 0f
                                }

                                if (animationMs >= animationDuration) {
                                    ++frame
                                    if (frame >= 4) {
                                        frame = 0
                                    }
                                    animationMs = 0f
                                }

                                animation = WALKING
                            } else {
                                animationMs = 0f
                                totalMs = movementDuration.toFloat()
                                frame = 0
                                animation = IDLE
                            }
                        }
                    }
                }

                val resource = when (animation) {
                    IDLE -> "hero_idle.png"
                    WALKING -> "hero_walking.png"
                }

                val bitmap = useResource(resource) { loadImageBitmap(it) }

                val columnIndex = when (animation) {
                    IDLE -> 0
                    WALKING -> frame
                }

                val rowIndex = when (direction) {
                    DOWN -> 0
                    LEFT -> 1
                    RIGHT -> 2
                    UP -> 3
                }
                val painter = BitmapPainter(bitmap, IntOffset(columnIndex * 16, rowIndex * 16), IntSize(16, 16), FilterQuality.None)

                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .offset(
                                x = animateDpAsState(
                                    x,
                                    animationSpec = tween(easing = LinearEasing, durationMillis = movementDuration)
                                ).value,
                                y = animateDpAsState(
                                    y,
                                    animationSpec = tween(easing = LinearEasing, durationMillis = movementDuration)
                                ).value
                            )
                            .size(64.dp)
                    )
                }
            }
        }
    }
}
