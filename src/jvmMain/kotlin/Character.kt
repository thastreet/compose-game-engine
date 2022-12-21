@file:OptIn(ExperimentalComposeUiApi::class)

import Animation.IDLE
import Animation.WALKING
import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

data class CharacterState(
    val x: Dp = 0.dp,
    val y: Dp = 0.dp,
    val direction: Direction = DOWN,
    val last: Int? = null,
    val animation: Animation = IDLE,
    val frame: Int = -1
) {
    val movementFrame = 4
    val movementDistance = 16.dp
    val directionKeys = setOf(Key.DirectionLeft, Key.DirectionRight, Key.DirectionUp, Key.DirectionDown)
}

@Composable
fun Character(
    idle: String,
    walking: String,
    totalFrame: Int,
    content: @Composable (painter: Painter, movementFrame: Int, x: Dp, y: Dp, handleKeyPressed: (pressedKeys: Set<Key>) -> Unit) -> Unit
) {
    val state = remember { mutableStateOf(CharacterState()) }
    val resource = when (state.value.animation) {
        IDLE -> idle
        WALKING -> walking
    }

    val bitmap = useResource(resource) { loadImageBitmap(it) }

    val columnIndex = when (state.value.animation) {
        IDLE -> 0
        WALKING -> state.value.frame
    }

    val rowIndex = when (state.value.direction) {
        DOWN -> 0
        LEFT -> 1
        RIGHT -> 2
        UP -> 3
    }
    val painter = BitmapPainter(
        bitmap,
        IntOffset(columnIndex * 16, rowIndex * 16),
        IntSize(16, 16),
        FilterQuality.None
    )

    content(painter, state.value.movementFrame, state.value.x, state.value.y) { pressedKeys: Set<Key> ->
        val move = state.value.last == null || totalFrame - (state.value.last ?: 0) >= state.value.movementFrame

        if (move) {
            state.value = state.value.copy(last = totalFrame)
        }

        val directionPressed = pressedKeys.intersect(state.value.directionKeys).isNotEmpty()

        if (directionPressed) {
            if (move) {
                when {
                    pressedKeys.contains(Key.DirectionLeft) -> {
                        state.value = state.value.copy(x = state.value.x - state.value.movementDistance, direction = LEFT)
                    }

                    pressedKeys.contains(Key.DirectionRight) -> {
                        state.value = state.value.copy(x = state.value.x + state.value.movementDistance, direction = RIGHT)
                    }

                    pressedKeys.contains(Key.DirectionUp) -> {
                        state.value = state.value.copy(y = state.value.y - state.value.movementDistance, direction = UP)
                    }

                    pressedKeys.contains(Key.DirectionDown) -> {
                        state.value = state.value.copy(y = state.value.y + state.value.movementDistance, direction = DOWN)
                    }
                }

                state.value = state.value.copy(frame = state.value.frame + 1)
                if (state.value.frame >= 4) {
                    state.value = state.value.copy(frame = 0)
                }
            }

            state.value = state.value.copy(animation = WALKING)
        } else {
            state.value = state.value.copy(animation = IDLE, last = null, frame = -1)
        }
    }

}