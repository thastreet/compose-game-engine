import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp

@Composable
fun Hero(totalFrame: Int, pressedKeys: Set<Key>) {
    Character(
        "hero_idle.png",
        "hero_walking.png",
        totalFrame
    ) { painter, movementFrame, x, y, handleKeyPressed ->
        handleKeyPressed(pressedKeys)

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .offset(
                    x = animateDpAsState(
                        x,
                        animationSpec = tween(easing = LinearEasing, durationMillis = movementFrame * frameDurationMs)
                    ).value,
                    y = animateDpAsState(
                        y,
                        animationSpec = tween(easing = LinearEasing, durationMillis = movementFrame * frameDurationMs)
                    ).value
                )
                .size(64.dp)
        )
    }
}