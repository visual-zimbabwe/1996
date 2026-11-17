package app.editorial1996.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.editorial1996.data.CoverRepository
import app.editorial1996.data.GalleryViewMode
import app.editorial1996.data.MagazineCover
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GalleryUiState(
    val visibleCovers: List<MagazineCover> = emptyList(),
    val categories: List<String> = listOf("ALL"),
    val searchQuery: String = "",
    val selectedMonth: Int = 0,
    val selectedCategory: String = "ALL",
    val sfwEnabled: Boolean = true,
    val viewMode: GalleryViewMode = GalleryViewMode.GRID,
    val selectedCover: MagazineCover? = null,
    val isSearchingLive: Boolean = false,
    val livePage: Int = 0,
    val liveTotalFound: Int? = null,
    val liveError: String? = null,
    val liveResultCount: Int = 0,
)

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CoverRepository(application.applicationContext)
    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private var curatedCovers: List<MagazineCover> = emptyList()
    private var liveCovers: List<MagazineCover> = emptyList()
    private var liveSearchJob: Job? = null

    private val _uiState = MutableStateFlow(
        GalleryUiState(
            sfwEnabled = prefs.getBoolean(KEY_SFW_ENABLED, true),
        ),
    )
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    init {
        curatedCovers = repository.getCuratedCovers()
        recomputeVisible()
    }

    fun onQueryChange(query: String) {
        liveSearchJob?.cancel()
        liveCovers = emptyList()
        _uiState.update {
            it.copy(
                searchQuery = query,
                livePage = 0,
                liveTotalFound = null,
                liveError = null,
            )
        }
        recomputeVisible()
    }

    fun onSelectMonth(month: Int) {
        liveSearchJob?.cancel()
        liveCovers = emptyList()
        _uiState.update {
            it.copy(
                selectedMonth = month,
                livePage = 0,
                liveTotalFound = null,
                liveError = null,
            )
        }
        recomputeVisible()
    }

    fun onSelectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        recomputeVisible()
    }

    fun onToggleSfw() {
        val nextSfw = !_uiState.value.sfwEnabled
        prefs.edit().putBoolean(KEY_SFW_ENABLED, nextSfw).apply()
        liveSearchJob?.cancel()
        liveCovers = emptyList()
        _uiState.update { state ->
            val nextCat = if (nextSfw && state.selectedCategory == "Adult (NSFW)") "ALL" else state.selectedCategory
            state.copy(
                sfwEnabled = nextSfw,
                selectedCategory = nextCat,
                livePage = 0,
                liveTotalFound = null,
                liveError = null,
            )
        }
        recomputeVisible()
    }

    fun onToggleViewMode() {
        _uiState.update { state ->
            val next = if (state.viewMode == GalleryViewMode.GRID) GalleryViewMode.INDEX else GalleryViewMode.GRID
            state.copy(viewMode = next)
        }
    }

    fun onOpenCover(cover: MagazineCover) {
        _uiState.update { it.copy(selectedCover = cover) }
    }

    fun onCloseCover() {
        _uiState.update { it.copy(selectedCover = null) }
    }

    fun onStepSelectedCover(delta: Int) {
        val state = _uiState.value
        val current = state.selectedCover ?: return
        val list = state.visibleCovers
        val idx = list.indexOfFirst { it.identifier == current.identifier }
        if (idx == -1) return
        val nextIdx = idx + delta
        if (nextIdx in list.indices) {
            _uiState.update { it.copy(selectedCover = list[nextIdx]) }
        }
    }

    fun triggerLiveArchiveSearch(loadNextPage: Boolean = false) {
        val state = _uiState.value
        if (state.isSearchingLive) return
        val targetPage = if (loadNextPage) state.livePage + 1 else 1

        liveSearchJob?.cancel()
        liveSearchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearchingLive = true, liveError = null) }
            try {
                val pageResult = repository.searchLive1996(
                    query = state.searchQuery,
                    month = state.selectedMonth,
                    sfwEnabled = state.sfwEnabled,
                    page = targetPage,
                )
                liveCovers = if (loadNextPage) {
                    (liveCovers + pageResult.items).distinctBy { it.identifier }
                } else {
                    pageResult.items
                }
                _uiState.update {
                    it.copy(
                        isSearchingLive = false,
                        livePage = targetPage,
                        liveTotalFound = pageResult.totalFound,
                        liveError = null,
                    )
                }
                recomputeVisible()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSearchingLive = false,
                        liveError = e.message ?: "Unable to reach archive.org",
                    )
                }
            }
        }
    }

    private fun recomputeVisible() {
        val state = _uiState.value
        val categories = repository.getAvailableCategories(state.sfwEnabled)
        val effectiveCategory = if (state.selectedCategory in categories) state.selectedCategory else "ALL"
        val visible = repository.filterCovers(
            curated = curatedCovers,
            liveResults = liveCovers,
            query = state.searchQuery,
            month = state.selectedMonth,
            category = effectiveCategory,
            sfwEnabled = state.sfwEnabled,
        )
        _uiState.update {
            it.copy(
                visibleCovers = visible,
                categories = categories,
                selectedCategory = effectiveCategory,
                liveResultCount = liveCovers.size,
            )
        }
    }

    companion object {
        private const val PREFS_NAME = "editorial_1996_prefs"
        private const val KEY_SFW_ENABLED = "sfw_enabled"
    }
}
