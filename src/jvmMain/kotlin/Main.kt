import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val movementDuration = 128
    val movementDistance = 8.dp

    Window(
        onCloseRequest = ::exitApplication
    ) {
        MaterialTheme {
            Surface {
                val requester = remember { FocusRequester() }
                val pressed = remember { mutableSetOf<Key>() }
                var prevTime = remember { 0L }
                var totalMs = remember { movementDuration.toFloat() }
                var x by remember { mutableStateOf(0.dp) }
                var y by remember { mutableStateOf(0.dp) }

                LaunchedEffect(Unit) {
                    requester.requestFocus()
                    while (true) {
                        withFrameNanos { time ->
                            val deltaMs = (time - prevTime) / (1000f * 1000f)
                            totalMs += deltaMs
                            prevTime = time

                            if (totalMs >= movementDuration) {
                                if (pressed.contains(Key.DirectionLeft)) {
                                    x -= movementDistance
                                } else if (pressed.contains(Key.DirectionRight)) {
                                    x += movementDistance
                                }
                                if (pressed.contains(Key.DirectionUp)) {
                                    y -= movementDistance
                                } else if (pressed.contains(Key.DirectionDown)) {
                                    y += movementDistance
                                }
                                totalMs = 0f
                            }

                            if (pressed.isEmpty()) {
                                totalMs = movementDuration.toFloat()
                            }
                        }
                    }
                }

                Box(
                    Modifier
                        .onPreviewKeyEvent { false }
                        .onKeyEvent {
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
                        .fillMaxSize()
                        .focusRequester(requester)
                        .focusable()
                ) {
                    Box(
                        Modifier
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
                            .size(50.dp)
                            .background(Color.Blue)
                    ) {

                    }
                }
            }
        }
    }
}
