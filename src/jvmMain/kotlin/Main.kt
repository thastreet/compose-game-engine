
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

enum class Animation {
    IDLE,
    WALKING
}

const val frameDurationMs = 60

fun main() = application {
    val pressedKeys = remember { mutableSetOf<Key>() }

    Window(
        onCloseRequest = ::exitApplication,
        resizable = false,
        onPreviewKeyEvent = {
            when {
                (it.type == KeyEventType.KeyDown) -> {
                    pressedKeys.add(it.key)
                    true
                }

                (it.type == KeyEventType.KeyUp) -> {
                    pressedKeys.remove(it.key)
                    true
                }

                else -> false
            }
        }
    ) {
        MaterialTheme {
            Surface {
                var prevTime = remember { 0L }
                var deltaCount = remember { 0f }
                var totalFrame by remember { mutableStateOf(0) }

                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    Hero(totalFrame, pressedKeys)
                }

                LaunchedEffect(Unit) {
                    while (true) {
                        withFrameNanos { time ->
                            val deltaMs = (time - prevTime) / (1000f * 1000f)
                            deltaCount += deltaMs
                            prevTime = time

                            if (deltaCount >= frameDurationMs) {
                                ++totalFrame
                                deltaCount = 0f
                            }
                        }
                    }
                }
            }
        }
    }
}
