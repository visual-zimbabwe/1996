package app.editorial1996.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@Serializable
internal data class IaSearchEnvelope(
    val response: IaSearchResponse = IaSearchResponse(),
)

@Serializable
internal data class IaSearchResponse(
    val numFound: Int = 0,
    val start: Int = 0,
    val docs: List<IaDoc> = emptyList(),
)

@Serializable
internal data class IaDoc(
    val identifier: String = "",
    val title: JsonElement? = null,
    val date: String? = null,
    val collection: JsonElement? = null,
    val subject: JsonElement? = null,
    val downloads: Int = 0,
)

data class LiveSearchPage(
    val items: List<MagazineCover>,
    val totalFound: Int,
)

object InternetArchiveApi {
    private const val SEARCH_ENDPOINT = "https://archive.org/advancedsearch.php"
    private const val USER_AGENT = "Editorial1996-Android/1.0"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val nsfwRegex = Regex(
        """\b(playboy|penthouse|hustler|oui|cheri|swank|high\s*society|club\s*international|leg\s*show|nude|erotica|adult\s*magazine|xxx|porn)\b""",
        RegexOption.IGNORE_CASE,
    )

    private val excludeNoiseRegex = Regex(
        """\b(index|microfilm|pack|compendium|199[0-5]\s*[-–]\s*1996|1996\s*[-–]\s*199[7-9]|complete\s*set)\b""",
        RegexOption.IGNORE_CASE,
    )

    private val yearRegex = Regex("""\b(19\d{2}|20\d{2})\b""")

    private val monthPatterns: List<Pair<Int, List<Regex>>> = listOf(
        1 to listOf(Regex("""\bjan(?:uary)?\b"""), Regex("""\b01[-_.]1996\b"""), Regex("""\b1996[-_.]01\b"""), Regex("""\b9601\b""")),
        2 to listOf(Regex("""\bfeb(?:ruary)?\b"""), Regex("""\b02[-_.]1996\b"""), Regex("""\b1996[-_.]02\b"""), Regex("""\b9602\b""")),
        3 to listOf(Regex("""\bmar(?:ch)?\b"""), Regex("""\b03[-_.]1996\b"""), Regex("""\b1996[-_.]03\b"""), Regex("""\b9603\b""")),
        4 to listOf(Regex("""\bapr(?:il)?\b"""), Regex("""\b04[-_.]1996\b"""), Regex("""\b1996[-_.]04\b"""), Regex("""\b9604\b""")),
        5 to listOf(Regex("""\bmay\b"""), Regex("""\b05[-_.]1996\b"""), Regex("""\b1996[-_.]05\b"""), Regex("""\b9605\b""")),
        6 to listOf(Regex("""\bjun(?:e)?\b"""), Regex("""\b06[-_.]1996\b"""), Regex("""\b1996[-_.]06\b"""), Regex("""\b9606\b""")),
        7 to listOf(Regex("""\bjul(?:y)?\b"""), Regex("""\b07[-_.]1996\b"""), Regex("""\b1996[-_.]07\b"""), Regex("""\b9607\b""")),
        8 to listOf(Regex("""\baug(?:ust)?\b"""), Regex("""\b08[-_.]1996\b"""), Regex("""\b1996[-_.]08\b"""), Regex("""\b9608\b""")),
        9 to listOf(Regex("""\bsep(?:tember|t)?\b"""), Regex("""\b09[-_.]1996\b"""), Regex("""\b1996[-_.]09\b"""), Regex("""\b9609\b""")),
        10 to listOf(Regex("""\boct(?:ober)?\b"""), Regex("""\b10[-_.]1996\b"""), Regex("""\b1996[-_.]10\b"""), Regex("""\b9610\b""")),
        11 to listOf(Regex("""\bnov(?:ember)?\b"""), Regex("""\b11[-_.]1996\b"""), Regex("""\b1996[-_.]11\b"""), Regex("""\b9611\b""")),
        12 to listOf(Regex("""\bdec(?:ember)?\b"""), Regex("""\b12[-_.]1996\b"""), Regex("""\b1996[-_.]12\b"""), Regex("""\b9612\b""")),
    )

    /**
     * Build a Lucene query strictly scoped to 1996 magazines on archive.org.
     */
    fun build1996Query(
        userQuery: String,
        month: Int,
        sfwEnabled: Boolean,
    ): String {
        val dateRange = if (month in 1..12) {
            val mm = month.toString().padStart(2, '0')
            val lastDay = when (month) {
                2 -> 29 // 1996 was a leap year
                4, 6, 9, 11 -> 30
                else -> 31
            }
            "date:[1996-$mm-01 TO 1996-$mm-$lastDay]"
        } else {
            "date:[1996-01-01 TO 1996-12-31]"
        }

        val parts = mutableListOf(
            "mediatype:texts",
            dateRange,
            "(collection:magazine_rack OR collection:periodicals)",
            "-collection:magazine_packs",
            "-collection:sim_microfilm",
        )

        if (sfwEnabled) {
            parts += "-collection:(adultmagazines OR pub_playboy OR pub_penthouse)"
            parts += "-title:(hustler OR playboy OR penthouse OR cheri OR swank)"
        }

        val cleanedQuery = userQuery
            .replace(Regex("""[^\p{L}\p{N}\s\-']"""), " ")
            .trim()
        if (cleanedQuery.isNotEmpty()) {
            val terms = cleanedQuery.split(Regex("""\s+"""))
                .filter { it.isNotBlank() }
                .joinToString(" AND ") { "\"$it\"" }
            parts += "($terms)"
        }

        return parts.joinToString(" AND ")
    }

    suspend fun search1996Magazines(
        userQuery: String,
        month: Int = 0,
        sfwEnabled: Boolean = true,
        page: Int = 1,
        rows: Int = 36,
    ): LiveSearchPage = withContext(Dispatchers.IO) {
        val lucene = build1996Query(userQuery, month, sfwEnabled)
        val queryString = buildString {
            append("q=").append(encode(lucene))
            append("&fl[]=").append(encode("identifier"))
            append("&fl[]=").append(encode("title"))
            append("&fl[]=").append(encode("date"))
            append("&fl[]=").append(encode("collection"))
            append("&fl[]=").append(encode("subject"))
            append("&fl[]=").append(encode("downloads"))
            append("&sort[]=").append(encode("downloads desc"))
            append("&rows=").append(rows)
            append("&page=").append(page)
            append("&output=json")
        }
        val url = URL("$SEARCH_ENDPOINT?$queryString")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 12_000
            readTimeout = 15_000
            setRequestProperty("User-Agent", USER_AGENT)
        }

        val rawJson = try {
            val code = conn.responseCode
            if (code !in 200..299) {
                throw IllegalStateException("Archive.org returned HTTP $code")
            }
            conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            conn.disconnect()
        }

        val envelope = json.decodeFromString<IaSearchEnvelope>(rawJson)
        val mapped = envelope.response.docs.mapNotNull { doc ->
            mapDocToCover(doc, sfwEnabled)
        }
        LiveSearchPage(
            items = mapped,
            totalFound = envelope.response.numFound,
        )
    }

    internal fun mapDocToCover(doc: IaDoc, sfwEnabled: Boolean): MagazineCover? {
        val ident = doc.identifier.trim()
        val title = extractString(doc.title).trim()
        if (ident.isEmpty() || title.isEmpty()) return null
        if (excludeNoiseRegex.containsMatchIn(title) || excludeNoiseRegex.containsMatchIn(ident)) return null

        val years = yearRegex.findAll(title).map { it.value }.toList()
        if (years.isNotEmpty() && "1996" !in years) return null

        val collections = extractStringList(doc.collection)
        val subjects = extractStringList(doc.subject)
        val isNsfw = isNsfwItem(title, ident, collections, subjects)
        if (sfwEnabled && isNsfw) return null

        val parsedMonth = parse1996Month(doc.date, title, ident)
        val trimmedTitle = if (title.length > 90) title.take(87).trimEnd() + "..." else title

        return MagazineCover(
            identifier = ident,
            title = trimmedTitle,
            category = inferCategory(collections, subjects),
            month = parsedMonth,
            dateLabel = MonthFilter.fullLabel(parsedMonth),
            nsfw = isNsfw,
            downloads = doc.downloads,
            thumbUrl = "https://archive.org/services/img/$ident",
            coverUrl = "https://archive.org/download/$ident/page/cover_w600.jpg",
            archiveUrl = "https://archive.org/details/$ident",
            isLiveResult = true,
        )
    }

    internal fun isNsfwItem(
        title: String,
        identifier: String,
        collections: List<String>,
        subjects: List<String>,
    ): Boolean {
        val colLower = collections.joinToString(" ").lowercase()
        if ("adultmagazines" in colLower || "pub_playboy" in colLower || "pub_penthouse" in colLower) {
            return true
        }
        val combined = "$title $identifier $colLower ${subjects.joinToString(" ")}"
        return nsfwRegex.containsMatchIn(combined)
    }

    internal fun parse1996Month(dateStr: String?, title: String, identifier: String): Int {
        val text = "$title $identifier".lowercase()
        for ((monthIdx, regexes) in monthPatterns) {
            if (regexes.any { it.containsMatchIn(text) }) {
                return monthIdx
            }
        }
        if (!dateStr.isNullOrBlank()) {
            val m = Regex("""^1996-(\d{2})-(\d{2})""").find(dateStr)
            if (m != null) {
                val month = m.groupValues[1].toIntOrNull() ?: 0
                val day = m.groupValues[2].toIntOrNull() ?: 0
                if (!(month == 1 && day == 1) && month in 1..12) {
                    return month
                }
            }
        }
        return 0
    }

    private fun inferCategory(collections: List<String>, subjects: List<String>): String {
        val joined = (collections + subjects).joinToString(" ").lowercase()
        return when {
            "adult" in joined || "playboy" in joined || "penthouse" in joined -> "Adult (NSFW)"
            "music" in joined || "rolling" in joined -> "Music"
            "fashion" in joined || "vogue" in joined -> "Fashion & Style"
            "film" in joined || "cinema" in joined || "movie" in joined -> "Film & TV"
            "science" in joined || "nature" in joined || "astronomy" in joined -> "Science & Nature"
            "sport" in joined || "auto" in joined || "skate" in joined -> "Sports & Auto"
            "computer" in joined || "videogame" in joined || "gaming" in joined -> "Tech & Gaming"
            "comic" in joined || "art" in joined -> "Comics & Art"
            "news" in joined || "culture" in joined -> "News & Culture"
            else -> "Archive.org"
        }
    }

    private fun extractString(el: JsonElement?): String {
        return when (el) {
            is JsonPrimitive -> el.content
            is JsonArray -> el.firstOrNull()?.let { (it as? JsonPrimitive)?.content } ?: ""
            else -> ""
        }
    }

    private fun extractStringList(el: JsonElement?): List<String> {
        return when (el) {
            is JsonPrimitive -> listOf(el.content)
            is JsonArray -> el.mapNotNull { (it as? JsonPrimitive)?.content }
            else -> emptyList()
        }
    }

    private fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")
}
