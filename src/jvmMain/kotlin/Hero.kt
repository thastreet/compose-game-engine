
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key

@Composable
fun Hero(totalFrame: Int, keyPressed: Set<Key>) {
    Character(
        "hero_idle.png",
        "hero_walking.png",
        totalFrame
    ) { painter, modifier, handleKeyPressed ->
        handleKeyPressed(keyPressed)

        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier
        )
    }
}