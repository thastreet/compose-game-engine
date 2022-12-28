
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

@Composable
fun Object(
    name: String,
    x: Dp,
    y: Dp,
    collisionDetector: CollisionDetector,
    stash: Stash
) {
    val state by remember { mutableStateOf(ObjectState(x, y, name)) }

    LaunchedEffect(Unit) {
        collisionDetector.updatePosition(state)
        stash.register(state.id, StashedObject(false, state.name))
    }

    if (stash.isPickedUp(state.id)) return

    val bitmap = useResource("objects.png") { loadImageBitmap(it) }

    val painter = BitmapPainter(
        bitmap,
        IntOffset(0, 0),
        IntSize(16, 16),
        FilterQuality.None
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .offset(x, y)
            .size(Consts.CHARACTER_SIZE)
    )
}