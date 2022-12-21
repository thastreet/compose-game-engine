import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp

@Composable
fun Hero(totalFrame: Int, pressedKeys: Set<Key>) {
    Character(
        "hero_idle.png",
        "hero_walking.png",
        totalFrame
    ) { painter, modifier, handleKeyPressed ->
        handleKeyPressed(pressedKeys)

        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier.size(64.dp)
        )
    }
}