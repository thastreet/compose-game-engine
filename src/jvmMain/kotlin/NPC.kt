import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import androidx.compose.runtime.Composable
import kotlin.random.Random

@Composable
fun NPC(totalFrame: Int) {
    Character(
        "npc_idle.png",
        "hero_walking.png",
        totalFrame,
        60,
        x = Consts.MOVEMENT_DISTANCE * 10,
        y = Consts.MOVEMENT_DISTANCE * 10
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