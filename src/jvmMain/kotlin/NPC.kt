
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable

@Composable
fun NPC(totalFrame: Int) {
    Character(
        "npc_idle.png",
        "hero_walking.png",
        totalFrame,
        x = Consts.MOVEMENT_DISTANCE * 10,
        y = Consts.MOVEMENT_DISTANCE * 10
    ) { painter, modifier, _ ->
        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier
        )
    }
}