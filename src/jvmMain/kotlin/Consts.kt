@file:OptIn(ExperimentalComposeUiApi::class)

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.dp

object Consts {
    const val FRAME_DURATION_MS = 16
    val CASE_SIZE = 32.dp
    val CHARACTER_SIZE = CASE_SIZE
    const val MOVEMENT_DURATION_MS = 320
    val DIRECTION_KEYS = setOf(Key.DirectionLeft, Key.DirectionRight, Key.DirectionUp, Key.DirectionDown)
}