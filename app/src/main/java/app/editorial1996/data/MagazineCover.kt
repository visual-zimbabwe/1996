package app.editorial1996.data

import kotlinx.serialization.Serializable

@Serializable
data class MagazineCover(
    val identifier: String,
    val title: String,
    val category: String = "Archive",
    val month: Int = 0,
    val dateLabel: String = "1996",
    val nsfw: Boolean = false,
    val downloads: Int = 0,
    val thumbUrl: String,
    val coverUrl: String,
    val archiveUrl: String,
    val isLiveResult: Boolean = false,
)

enum class GalleryViewMode {
    GRID,
    INDEX,
}

object MonthFilter {
    val ALL = listOf(
        0 to "ALL",
        1 to "JAN",
        2 to "FEB",
        3 to "MAR",
        4 to "APR",
        5 to "MAY",
        6 to "JUN",
        7 to "JUL",
        8 to "AUG",
        9 to "SEP",
        10 to "OCT",
        11 to "NOV",
        12 to "DEC",
    )

    private val FULL_NAMES = arrayOf(
        "",
        "JANUARY",
        "FEBRUARY",
        "MARCH",
        "APRIL",
        "MAY",
        "JUNE",
        "JULY",
        "AUGUST",
        "SEPTEMBER",
        "OCTOBER",
        "NOVEMBER",
        "DECEMBER",
    )

    fun fullLabel(month: Int): String {
        return if (month in 1..12) "${FULL_NAMES[month]} 1996" else "1996"
    }
}
