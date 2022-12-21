@file:OptIn(ExperimentalComposeUiApi::class)

import Animation.IDLE
import Animation.WALKING
import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
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
    val lastMovementFrame: Int? = null,
    val animation: Animation = IDLE,
    val animationFrame: Int = -1
) {
    val movementFrame = 4
    val directionKeys = setOf(Key.DirectionLeft, Key.DirectionRight, Key.DirectionUp, Key.DirectionDown)
}

private fun handleKeyPressed(state: MutableState<CharacterState>, totalFrame: Int, pressedKeys: Set<Key>) {
    val move = state.value.lastMovementFrame == null || totalFrame - (state.value.lastMovementFrame
        ?: 0) >= state.value.movementFrame

    if (move) {
        state.value = state.value.copy(lastMovementFrame = totalFrame)
    }

    val directionPressed = pressedKeys.intersect(state.value.directionKeys).isNotEmpty()

    if (directionPressed) {
        if (move) {
            when {
                pressedKeys.contains(Key.DirectionLeft) -> {
                    state.value = state.value.copy(x = state.value.x - Consts.MOVEMENT_DISTANCE, direction = LEFT)
                }

                pressedKeys.contains(Key.DirectionRight) -> {
                    state.value = state.value.copy(x = state.value.x + Consts.MOVEMENT_DISTANCE, direction = RIGHT)
                }

                pressedKeys.contains(Key.DirectionUp) -> {
                    state.value = state.value.copy(y = state.value.y - Consts.MOVEMENT_DISTANCE, direction = UP)
                }

                pressedKeys.contains(Key.DirectionDown) -> {
                    state.value = state.value.copy(y = state.value.y + Consts.MOVEMENT_DISTANCE, direction = DOWN)
                }
            }

            state.value = state.value.copy(animationFrame = state.value.animationFrame + 1)
            if (state.value.animationFrame >= 4) {
                state.value = state.value.copy(animationFrame = 0)
            }
        }

        state.value = state.value.copy(animation = WALKING)
    } else {
        state.value = state.value.copy(animation = IDLE, lastMovementFrame = null, animationFrame = -1)
    }
}

@Composable
fun Character(
    idle: String,
    walking: String,
    totalFrame: Int,
    x: Dp = 0.dp,
    y: Dp = 0.dp,
    content: @Composable (painter: Painter, modifier: Modifier, handleKeyPressed: (keyPressed: Set<Key>) -> Unit) -> Unit
) {
    val state = remember { mutableStateOf(CharacterState(x = x, y = y)) }
    val resource = when (state.value.animation) {
        IDLE -> idle
        WALKING -> walking
    }

    val bitmap = useResource(resource) { loadImageBitmap(it) }

    val columnIndex = when (state.value.animation) {
        IDLE -> 0
        WALKING -> state.value.animationFrame
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

    content(
        painter,
        Modifier
            .offset(
                animateDpAsState(
                    state.value.x,
                    animationSpec = tween(
                        easing = LinearEasing,
                        durationMillis = state.value.movementFrame * Consts.FRAME_DURATION_MS
                    )
                ).value,
                animateDpAsState(
                    state.value.y,
                    animationSpec = tween(
                        easing = LinearEasing,
                        durationMillis = state.value.movementFrame * Consts.FRAME_DURATION_MS
                    )
                ).value
            )
            .size(Consts.CHARACTER_SIZE)
    ) { pressedKeys: Set<Key> ->
        handleKeyPressed(state, totalFrame, pressedKeys)
    }
}