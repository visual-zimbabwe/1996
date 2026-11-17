package app.editorial1996.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.editorial1996.data.MagazineCover
import app.editorial1996.ui.theme.EditorialColors
import app.editorial1996.ui.theme.EditorialTextStyles
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun CoverDetailScreen(
    cover: MagazineCover,
    canGoPrev: Boolean,
    canGoNext: Boolean,
    onClose: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    val context = LocalContext.current
    var scale by remember(cover.identifier) { mutableFloatStateOf(1f) }
    var offset by remember(cover.identifier) { mutableStateOf(Offset.Zero) }

    BackHandler(onBack = onClose)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialColors.CanvasBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        // Top typographic bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TypographicAction(
                label = "[ CLOSE ]",
                active = true,
                onClick = onClose,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                TypographicAction(
                    label = "PREV",
                    active = canGoPrev,
                    onClick = { if (canGoPrev) onPrev() },
                )
                TypographicAction(
                    label = "NEXT",
                    active = canGoNext,
                    onClick = { if (canGoNext) onNext() },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Zoomable 0px-radius print reproduction
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(EditorialColors.CanvasBg)
                .clipToBounds()
                .pointerInput(cover.identifier) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1.05f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale = 2.2f
                            }
                        },
                    )
                }
                .pointerInput(cover.identifier) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val newScale = (scale * zoom).coerceIn(1f, 4f)
                        scale = newScale
                        offset = if (newScale > 1f) offset + pan else Offset.Zero
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(cover.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = cover.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y,
                    ),
                error = {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(cover.thumbUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = cover.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Exhibition Placard
        Text(
            text = "${cover.dateLabel}  ·  ${cover.category.uppercase()}",
            style = EditorialTextStyles.DateStamp,
            color = EditorialColors.SecondaryInk,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = cover.title,
            style = EditorialTextStyles.HeadlineMedium,
            color = EditorialColors.PrimaryInk,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TypographicAction(
                label = "READ ISSUE ON ARCHIVE.ORG",
                active = true,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cover.archiveUrl))
                    context.startActivity(intent)
                },
            )
            Text(
                text = if (scale > 1.05f) "DOUBLE-TAP RESET" else "PINCH TO ZOOM",
                style = EditorialTextStyles.LabelCaps,
                color = EditorialColors.SecondaryInk,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
