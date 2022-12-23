@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp

@Composable
fun Hero(totalFrame: Int, keyPressed: Set<Key>, collisionDetector: CollisionDetector) {
    Character(
        "hero_idle.png",
        "hero_walking.png",
        totalFrame,
        20,
        0.dp,
        0.dp,
        collisionDetector
    ) { state, handleKeyPressed, _, _ ->
        handleKeyPressed(keyPressed)

        val collisionInfo = collisionDetector.facing(state.value)
        if (keyPressed.contains(Key.A) && collisionInfo != null && collisionInfo.canBePickedUp) {
            Stash.pickup(collisionInfo.id)
            collisionDetector.remove(collisionInfo.id)
        }
    }
}