@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp

@Composable
fun Hero(totalFrame: Int, pressedKeys: Set<Key>, collisionDetector: CollisionDetector, stash: Stash, shouldHandleKeyPressed: Boolean) {
    Character(
        "hero_idle.png",
        "hero_walking.png",
        totalFrame,
        20,
        0.dp,
        0.dp,
        collisionDetector
    ) { state, handleKeyPressed, _, _ ->
        if (shouldHandleKeyPressed) {
            handleKeyPressed(pressedKeys)
        }

        val collisionInfo = collisionDetector.facing(state.value)
        if (pressedKeys.contains(Key.A) && collisionInfo != null && collisionInfo.canBePickedUp) {
            stash.pickup(collisionInfo.id)
            collisionDetector.remove(collisionInfo.id)
        }
    }
}