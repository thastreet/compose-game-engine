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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CharacterState(
    val movementFrame: Int,
    val x: Dp = 0.dp,
    val y: Dp = 0.dp,
    val direction: Direction = DOWN,
    val lastMovementFrame: Int? = null,
    val animation: Animation = IDLE,
    val animationFrame: Int = -1
) {
    val directionKeys = setOf(Key.DirectionLeft, Key.DirectionRight, Key.DirectionUp, Key.DirectionDown)

    fun shouldMove(totalFrame: Int) = lastMovementFrame == null || totalFrame - lastMovementFrame >= movementFrame
}

private fun move(state: MutableState<CharacterState>, totalFrame: Int, direction: Direction) {
    state.value = state.value.copy(lastMovementFrame = totalFrame)

    when (direction) {
        DOWN -> state.value = state.value.copy(y = state.value.y + Consts.MOVEMENT_DISTANCE, direction = DOWN)
        LEFT -> state.value = state.value.copy(x = state.value.x - Consts.MOVEMENT_DISTANCE, direction = LEFT)
        RIGHT -> state.value = state.value.copy(x = state.value.x + Consts.MOVEMENT_DISTANCE, direction = RIGHT)
        UP -> state.value = state.value.copy(y = state.value.y - Consts.MOVEMENT_DISTANCE, direction = UP)
    }

    state.value = state.value.copy(animationFrame = state.value.animationFrame + 1, animation = WALKING)
    if (state.value.animationFrame >= 4) {
        state.value = state.value.copy(animationFrame = 0)
    }
}

private fun endAnimation(state: MutableState<CharacterState>) {
    state.value = state.value.copy(animation = IDLE, animationFrame = -1)
}

private fun handleKeyPressed(state: MutableState<CharacterState>, totalFrame: Int, pressedKeys: Set<Key>) {
    val directionPressed = pressedKeys.intersect(state.value.directionKeys).isNotEmpty()

    if (directionPressed) {
        if (state.value.shouldMove(totalFrame)) {
            move(
                state,
                totalFrame,
                when {
                    pressedKeys.contains(Key.DirectionLeft) -> LEFT
                    pressedKeys.contains(Key.DirectionRight) -> RIGHT
                    pressedKeys.contains(Key.DirectionUp) -> UP
                    pressedKeys.contains(Key.DirectionDown) -> DOWN
                    else -> throw IllegalArgumentException("Unsupported pressed key")
                }
            )
        }
    } else {
        endAnimation(state)
        state.value = state.value.copy(lastMovementFrame = null)
    }
}

@Composable
fun Character(
    idle: String,
    walking: String,
    totalFrame: Int,
    movementFrame: Int,
    x: Dp = 0.dp,
    y: Dp = 0.dp,
    actions: @Composable (
        handleKeyPressed: (keyPressed: Set<Key>) -> Unit,
        move: (direction: Direction) -> Unit,
        shouldMove: Boolean
    ) -> Unit
) {
    val state = remember { mutableStateOf(CharacterState(movementFrame = movementFrame, x = x, y = y)) }
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

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .offset(
                animateDpAsState(
                    state.value.x,
                    animationSpec = tween(
                        easing = LinearEasing,
                        durationMillis = Consts.MOVEMENT_DURATION_MS
                    )
                ).value,
                animateDpAsState(
                    state.value.y,
                    animationSpec = tween(
                        easing = LinearEasing,
                        durationMillis = Consts.MOVEMENT_DURATION_MS
                    )
                ).value
            )
            .size(Consts.CHARACTER_SIZE)
    )

    val scope = rememberCoroutineScope()

    actions(
        { pressedKeys: Set<Key> ->
            handleKeyPressed(state, totalFrame, pressedKeys)
        },
        { direction ->
            move(state, totalFrame, direction)
            scope.launch {
                delay(Consts.MOVEMENT_DURATION_MS.toLong())
                endAnimation(state)
            }
        },
        state.value.shouldMove(totalFrame)
    )
}