
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
    x: Dp,
    y: Dp,
    canBePickedUp: Boolean,
    collisionDetector: CollisionDetector
) {
    val state by remember { mutableStateOf(ObjectState(x, y, canBePickedUp)) }
    val pickedUp = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        collisionDetector.updatePosition(state)
        Stash.register(state, pickedUp)
    }

    if (pickedUp.value) return

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