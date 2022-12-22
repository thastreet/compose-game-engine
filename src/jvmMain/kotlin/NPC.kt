import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import kotlin.random.Random

@Composable
fun NPC(
    x: Dp,
    y: Dp,
    totalFrame: Int
) {
    Character(
        "npc_idle.png",
        "npc_walking.png",
        totalFrame,
        200,
        x = x,
        y = y
    ) { _, move, shouldMove ->
        if (shouldMove) {
            Random.nextInt(4).let { direction ->
                when (direction) {
                    0 -> move(UP)
                    1 -> move(LEFT)
                    2 -> move(RIGHT)
                    3 -> move(DOWN)
                }
            }
        }
    }
}