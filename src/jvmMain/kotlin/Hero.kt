import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key

@Composable
fun Hero(totalFrame: Int, keyPressed: Set<Key>) {
    Character(
        "hero_idle.png",
        "hero_walking.png",
        totalFrame,
        4
    ) { handleKeyPressed, _, _ ->
        handleKeyPressed(keyPressed)
    }
}