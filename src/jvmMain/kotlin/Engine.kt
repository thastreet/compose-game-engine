import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.withFrameNanos

@Composable
fun Engine(totalFrame: MutableState<Int>) {
    LaunchedEffect(Unit) {
        var prevTime = 0L
        var deltaCount = 0f

        while (true) {
            withFrameNanos { time ->
                val deltaMs = (time - prevTime) / (1000f * 1000f)
                deltaCount += deltaMs
                prevTime = time

                if (deltaCount >= Consts.FRAME_DURATION_MS) {
                    ++totalFrame.value
                    deltaCount = 0f
                }
            }
        }
    }
}