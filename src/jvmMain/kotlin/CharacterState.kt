@file:OptIn(ExperimentalComposeUiApi::class)

import Animation.IDLE
import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.UUID

data class CharacterState(
    val movementFrame: Int,
    override val x: Dp = 0.dp,
    override val y: Dp = 0.dp,
    val direction: Direction = DOWN,
    val lastMovementFrame: Int? = null,
    val lastAnimationFrame: Int? = null,
    val animation: Animation = IDLE,
    val animationFrame: Int = -1,
    override val id: String = UUID.randomUUID().toString(),
    val animating: Boolean = false
) : State {
    fun shouldMove(totalFrame: Int) = lastMovementFrame == null || totalFrame - lastMovementFrame >= movementFrame
    fun shouldAnimate(totalFrame: Int) = lastAnimationFrame == null || totalFrame - lastAnimationFrame >= 10

    override val size = Size(Consts.CHARACTER_SIZE.value, Consts.CHARACTER_SIZE.value)

    fun getProjectedRect(direction: Direction): Rect =
        when (direction) {
            DOWN -> Rect(Offset(x.value, (y + Consts.CASE_SIZE).value), size)
            LEFT -> Rect(Offset((x - Consts.CASE_SIZE).value, y.value), size)
            RIGHT -> Rect(Offset((x + Consts.CASE_SIZE).value, y.value), size)
            UP -> Rect(Offset(x.value, (y - Consts.CASE_SIZE).value), size)
        }
}