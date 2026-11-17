package app.editorial1996.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.editorial1996.data.GalleryViewMode
import app.editorial1996.data.MagazineCover
import app.editorial1996.data.MonthFilter
import app.editorial1996.ui.theme.EditorialColors
import app.editorial1996.ui.theme.EditorialTextStyles
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest

@Composable
fun GalleryScreen(
    state: GalleryUiState,
    onQueryChange: (String) -> Unit,
    onSelectMonth: (Int) -> Unit,
    onSelectCategory: (String) -> Unit,
    onToggleSfw: () -> Unit,
    onToggleViewMode: () -> Unit,
    onOpenCover: (MagazineCover) -> Unit,
    onSearchArchiveLive: (Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EditorialColors.CanvasBg)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // Quiet top utility row: pure typographic controls, no containers or icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TypographicAction(
                label = if (state.viewMode == GalleryViewMode.GRID) "[ INDEX ]" else "[ GRID ]",
                active = false,
                onClick = onToggleViewMode,
            )
            Text(
                text = "${state.visibleCovers.size} ISSUES",
                style = EditorialTextStyles.LabelCaps,
                color = EditorialColors.SecondaryInk,
            )
            TypographicAction(
                label = if (state.sfwEnabled) "[ SFW: ON ]" else "[ SFW: OFF ]",
                active = !state.sfwEnabled,
                onClick = onToggleSfw,
            )
        }

        // Brand Wordmark
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "1996",
                style = EditorialTextStyles.DisplayBrand,
                color = EditorialColors.PrimaryInk,
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Borderless Search Prompt on raw #F0F0F0 canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                BasicTextField(
                    value = state.searchQuery,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = EditorialTextStyles.BodyLarge.copy(
                        color = EditorialColors.PrimaryInk,
                        textAlign = TextAlign.Center,
                    ),
                    cursorBrush = SolidColor(EditorialColors.PrimaryInk),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            onSearchArchiveLive(false)
                        },
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (state.searchQuery.isEmpty()) {
                                Text(
                                    text = "Type an issue, month, or publication...",
                                    style = EditorialTextStyles.BodyLarge.copy(fontStyle = FontStyle.Italic),
                                    color = EditorialColors.SecondaryInk,
                                    textAlign = TextAlign.Center,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
            }

            if (state.searchQuery.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TypographicAction(
                        label = if (state.isSearchingLive) "SEARCHING 1996 ARCHIVE..." else "SEARCH ARCHIVE.ORG (1996)",
                        active = true,
                        onClick = {
                            focusManager.clearFocus()
                            onSearchArchiveLive(false)
                        },
                    )
                    TypographicAction(
                        label = "CLEAR",
                        active = false,
                        onClick = { onQueryChange("") },
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Month Filter Strip
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(MonthFilter.ALL, key = { it.first }) { (monthInt, label) ->
                TypographicAction(
                    label = label,
                    active = state.selectedMonth == monthInt,
                    onClick = { onSelectMonth(monthInt) },
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Genre Filter Strip
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(state.categories, key = { it }) { category ->
                TypographicAction(
                    label = category.uppercase(),
                    active = state.selectedCategory == category,
                    onClick = { onSelectCategory(category) },
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (state.visibleCovers.isEmpty() && !state.isSearchingLive) {
            EmptyGalleryState(
                query = state.searchQuery,
                liveError = state.liveError,
                onSearchArchive = { onSearchArchiveLive(false) },
                onClearFilters = {
                    onQueryChange("")
                    onSelectMonth(0)
                    onSelectCategory("ALL")
                },
            )
        } else if (state.viewMode == GalleryViewMode.GRID) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 12.dp,
                    bottom = 48.dp,
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                items(
                    items = state.visibleCovers,
                    key = { it.identifier },
                ) { cover ->
                    MagazineCoverCard(
                        cover = cover,
                        onClick = { onOpenCover(cover) },
                    )
                }

                item(span = { GridItemSpan(2) }) {
                    ArchiveFooterSection(
                        state = state,
                        onLoadMore = { onSearchArchiveLive(true) },
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 12.dp,
                    bottom = 48.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                items(
                    items = state.visibleCovers,
                    key = { it.identifier },
                ) { cover ->
                    IndexCoverRow(
                        cover = cover,
                        onClick = { onOpenCover(cover) },
                    )
                }

                item {
                    ArchiveFooterSection(
                        state = state,
                        onLoadMore = { onSearchArchiveLive(true) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MagazineCoverCard(
    cover: MagazineCover,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        // Strict 0.dp corners, zero shadow, zero border per DESIGN.md
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .background(EditorialColors.CoverPlaceholder),
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(cover.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = cover.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = {
                    // Fallback to archive.org service thumbnail if cover_w600 fails
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(cover.thumbUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = cover.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = cover.dateLabel,
            style = EditorialTextStyles.DateStamp,
            color = EditorialColors.SecondaryInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = cover.title,
            style = EditorialTextStyles.HeadlineSmall,
            color = EditorialColors.PrimaryInk,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun IndexCoverRow(
    cover: MagazineCover,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = cover.title,
            style = EditorialTextStyles.BodyLarge,
            color = EditorialColors.PrimaryInk,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
                .alignByBaseline(),
        )
        Text(
            text = cover.dateLabel,
            style = EditorialTextStyles.DateStamp,
            color = EditorialColors.SecondaryInk,
            modifier = Modifier.alignByBaseline(),
        )
    }
}

@Composable
private fun ArchiveFooterSection(
    state: GalleryUiState,
    onLoadMore: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.liveError != null) {
            Text(
                text = state.liveError.uppercase(),
                style = EditorialTextStyles.LabelCaps,
                color = EditorialColors.ErrorInk,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (state.liveTotalFound != null) {
            Text(
                text = "ARCHIVE.ORG (1996): ${state.liveTotalFound} MATCHING ISSUES",
                style = EditorialTextStyles.LabelCaps,
                color = EditorialColors.SecondaryInk,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        TypographicAction(
            label = when {
                state.isSearchingLive -> "FETCHING 1996 ISSUES FROM ARCHIVE.ORG..."
                state.livePage == 0 -> "EXPLORE MORE 1996 COVERS ON ARCHIVE.ORG"
                else -> "LOAD MORE 1996 ISSUES"
            },
            active = true,
            onClick = onLoadMore,
        )
    }
}

@Composable
private fun EmptyGalleryState(
    query: String,
    liveError: String?,
    onSearchArchive: () -> Unit,
    onClearFilters: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "NO 1996 ISSUE IN CURATED WALL",
            style = EditorialTextStyles.LabelCaps,
            color = EditorialColors.SecondaryInk,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(14.dp))
        if (liveError != null) {
            Text(
                text = liveError,
                style = EditorialTextStyles.BodyMedium,
                color = EditorialColors.ErrorInk,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
        TypographicAction(
            label = if (query.isNotBlank()) "SEARCH ARCHIVE.ORG FOR \"${query.uppercase()}\"" else "QUERY FULL 1996 ARCHIVE",
            active = true,
            onClick = onSearchArchive,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TypographicAction(
            label = "RESET FILTERS",
            active = false,
            onClick = onClearFilters,
        )
    }
}

@Composable
fun TypographicAction(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        style = EditorialTextStyles.LabelCaps.copy(
            textDecoration = if (active) TextDecoration.Underline else TextDecoration.None,
        ),
        color = if (active) EditorialColors.PrimaryInk else EditorialColors.SecondaryInk,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = 4.dp),
    )
}
