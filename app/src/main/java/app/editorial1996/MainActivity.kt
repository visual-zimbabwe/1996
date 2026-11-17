package app.editorial1996

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import app.editorial1996.ui.CoverDetailScreen
import app.editorial1996.ui.GalleryScreen
import app.editorial1996.ui.GalleryViewModel
import app.editorial1996.ui.theme.Editorial1996Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            Editorial1996Theme {
                val vm: GalleryViewModel = viewModel()
                val state by vm.uiState.collectAsStateWithLifecycle()

                val selected = state.selectedCover
                if (selected != null) {
                    val idx = state.visibleCovers.indexOfFirst { it.identifier == selected.identifier }
                    CoverDetailScreen(
                        cover = selected,
                        canGoPrev = idx > 0,
                        canGoNext = idx in 0 until (state.visibleCovers.size - 1),
                        onClose = vm::onCloseCover,
                        onPrev = { vm.onStepSelectedCover(-1) },
                        onNext = { vm.onStepSelectedCover(1) },
                    )
                } else {
                    GalleryScreen(
                        state = state,
                        onQueryChange = vm::onQueryChange,
                        onSelectMonth = vm::onSelectMonth,
                        onSelectCategory = vm::onSelectCategory,
                        onToggleSfw = vm::onToggleSfw,
                        onToggleViewMode = vm::onToggleViewMode,
                        onOpenCover = vm::onOpenCover,
                        onSearchArchiveLive = vm::triggerLiveArchiveSearch,
                    )
                }
            }
        }
    }
}
