import Direction.RIGHT
import androidx.compose.runtime.Composable

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
            move(RIGHT)
        }
    }
}