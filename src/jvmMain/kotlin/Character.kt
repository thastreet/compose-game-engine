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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun Character(
    idle: String,
    walking: String,
    totalFrame: Int,
    movementFrame: Int,
    x: Dp,
    y: Dp,
    collisionDetector: CollisionDetector,
    content: @Composable (
        state: MutableState<CharacterState>,
        handleKeyPressed: (pressedKeys: Set<Key>) -> Unit,
        move: (direction: Direction) -> Unit,
        shouldMove: Boolean
    ) -> Unit
) {
    val state = remember { mutableStateOf(CharacterState(movementFrame = movementFrame, x = x, y = y)) }
    val idleImage: ImageBitmap by remember { mutableStateOf(useResource(idle) { loadImageBitmap(it) }) }
    val walkingImage: ImageBitmap by remember { mutableStateOf(useResource(walking) { loadImageBitmap(it) }) }

    val image = when (state.value.animation) {
        IDLE -> idleImage
        WALKING -> walkingImage
    }

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
        image,
        IntOffset(columnIndex * 16, rowIndex * 16),
        IntSize(16, 16),
        FilterQuality.None
    )

    var lastX: Dp? by remember { mutableStateOf(null) }
    var lastY: Dp? by remember { mutableStateOf(null) }

    if (lastX != null && lastX != state.value.x || lastY != null && lastY != state.value.y) {
        state.value = state.value.copy(animating = true)
    }

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
                ) { state.value = state.value.copy(animating = false) }.value,
                animateDpAsState(
                    state.value.y,
                    animationSpec = tween(
                        easing = LinearEasing,
                        durationMillis = Consts.MOVEMENT_DURATION_MS
                    )
                ) { state.value = state.value.copy(animating = false) }.value
            )
            .size(Consts.CHARACTER_SIZE)
    )

    lastX = state.value.x
    lastY = state.value.y

    val scope = rememberCoroutineScope()

    content(
        state,
        { pressedKeys: Set<Key> ->
            handleKeyPressed(state, totalFrame, pressedKeys, collisionDetector)
        },
        { direction ->
            move(state, totalFrame, direction, collisionDetector)
            animate(state, totalFrame)
            scope.launch {
                delay(Consts.MOVEMENT_DURATION_MS.toLong())
                endAnimation(state)
            }
        },
        state.value.shouldMove(totalFrame)
    )

    LaunchedEffect(Unit) {
        collisionDetector.updatePosition(state.value)
    }
}

private fun move(state: MutableState<CharacterState>, totalFrame: Int, direction: Direction, collisionDetector: CollisionDetector) {
    if (state.value.animating) {
        return
    }

    state.value = state.value.copy(lastMovementFrame = totalFrame)

    when (direction) {
        DOWN -> {
            state.value = state.value.copy(direction = DOWN)
            if (!collisionDetector.detectCollision(state.value)) {
                state.value = state.value.copy(y = state.value.y + Consts.MOVEMENT_DISTANCE)
            }
        }

        LEFT -> {
            state.value = state.value.copy(direction = LEFT)
            if (!collisionDetector.detectCollision(state.value)) {
                state.value = state.value.copy(x = state.value.x - Consts.MOVEMENT_DISTANCE)
            }
        }

        RIGHT -> {
            state.value = state.value.copy(direction = RIGHT)
            if (!collisionDetector.detectCollision(state.value)) {
                state.value = state.value.copy(x = state.value.x + Consts.MOVEMENT_DISTANCE)
            }
        }

        UP -> {
            state.value = state.value.copy(direction = UP)
            if (!collisionDetector.detectCollision(state.value)) {
                state.value = state.value.copy(y = state.value.y - Consts.MOVEMENT_DISTANCE)
            }
        }
    }

    collisionDetector.updatePosition(state.value)
}

private fun animate(state: MutableState<CharacterState>, totalFrame: Int) {
    state.value = state.value.copy(lastAnimationFrame = totalFrame, animationFrame = state.value.animationFrame + 1, animation = WALKING)
    if (state.value.animationFrame >= 4) {
        state.value = state.value.copy(animationFrame = 0)
    }
}

private fun endAnimation(state: MutableState<CharacterState>) {
    state.value = state.value.copy(animation = IDLE, animationFrame = -1)
}

private fun handleKeyPressed(state: MutableState<CharacterState>, totalFrame: Int, pressedKeys: Set<Key>, collisionDetector: CollisionDetector) {
    val directionPressed = pressedKeys.intersect(Consts.DIRECTION_KEYS).isNotEmpty()

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
                },
                collisionDetector
            )
        }

        if (state.value.shouldAnimate(totalFrame)) {
            animate(state, totalFrame)
        }
    } else {
        endAnimation(state)
        state.value = state.value.copy(lastMovementFrame = null)
    }
}