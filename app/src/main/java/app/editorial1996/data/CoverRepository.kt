package app.editorial1996.data

import android.content.Context
import kotlinx.serialization.json.Json

class CoverRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    private val cachedCuratedCovers: List<MagazineCover> by lazy {
        context.assets.open("covers_1996.json").bufferedReader(Charsets.UTF_8).use { reader ->
            json.decodeFromString<List<MagazineCover>>(reader.readText())
        }
    }

    fun getCuratedCovers(): List<MagazineCover> = cachedCuratedCovers

    fun getAvailableCategories(sfwEnabled: Boolean): List<String> {
        val base = cachedCuratedCovers
            .filter { !sfwEnabled || !it.nsfw }
            .map { it.category }
            .distinct()
        return listOf("ALL") + base
    }

    fun filterCovers(
        curated: List<MagazineCover>,
        liveResults: List<MagazineCover>,
        query: String,
        month: Int,
        category: String,
        sfwEnabled: Boolean,
    ): List<MagazineCover> {
        val q = query.trim().lowercase()
        val seen = HashSet<String>()
        val combined = ArrayList<MagazineCover>(curated.size + liveResults.size)

        // When user has searched live or loaded more, show matching curated first, then unique live results
        for (item in curated + liveResults) {
            if (!seen.add(item.identifier)) continue
            if (sfwEnabled && item.nsfw) continue
            if (month != 0 && item.month != month) continue
            if (category != "ALL" && !item.isLiveResult && item.category != category) continue
            if (q.isNotEmpty()) {
                val haystack = "${item.title} ${item.dateLabel} ${item.category} ${item.identifier}".lowercase()
                if (!haystack.contains(q)) continue
            }
            combined.add(item)
        }
        return combined
    }

    suspend fun searchLive1996(
        query: String,
        month: Int,
        sfwEnabled: Boolean,
        page: Int,
    ): LiveSearchPage {
        return InternetArchiveApi.search1996Magazines(
            userQuery = query,
            month = month,
            sfwEnabled = sfwEnabled,
            page = page,
        )
    }
}
