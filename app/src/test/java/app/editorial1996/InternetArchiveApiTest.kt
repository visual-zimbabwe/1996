package app.editorial1996

import app.editorial1996.data.IaDoc
import app.editorial1996.data.InternetArchiveApi
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InternetArchiveApiTest {

    @Test
    fun build1996Query_alwaysConstrainsYearTo1996() {
        val allMonthsQuery = InternetArchiveApi.build1996Query(
            userQuery = "rolling stone",
            month = 0,
            sfwEnabled = true,
        )
        assertTrue(allMonthsQuery.contains("date:[1996-01-01 TO 1996-12-31]"))
        assertTrue(allMonthsQuery.contains("\"rolling\" AND \"stone\""))
        assertTrue(allMonthsQuery.contains("-collection:(adultmagazines OR pub_playboy OR pub_penthouse)"))

        val febLeapQuery = InternetArchiveApi.build1996Query(
            userQuery = "",
            month = 2,
            sfwEnabled = false,
        )
        assertTrue(febLeapQuery.contains("date:[1996-02-01 TO 1996-02-29]"))
        assertFalse(febLeapQuery.contains("-collection:(adultmagazines"))
    }

    @Test
    fun mapDocToCover_respectsSfwToggleAndExcludesNoise() {
        val adultDoc = IaDoc(
            identifier = "Hustler-Hard-Drive-1996-01",
            title = JsonPrimitive("Hustler Hard Drive Vol. 2 Issue 1 (January 1996)"),
            date = "1996-11-30T00:00:00Z",
            downloads = 16583,
        )
        assertNull(InternetArchiveApi.mapDocToCover(adultDoc, sfwEnabled = true))
        val allowed = InternetArchiveApi.mapDocToCover(adultDoc, sfwEnabled = false)
        assertNotNull(allowed)
        assertTrue(allowed!!.nsfw)
        assertEquals(1, allowed.month)
        assertEquals("JANUARY 1996", allowed.dateLabel)

        val indexDoc = IaDoc(
            identifier = "sim_playboy_1996_43_index",
            title = JsonPrimitive("Playboy 1996: Vol 43 Index"),
            date = "1996-01-01T00:00:00Z",
        )
        assertNull(InternetArchiveApi.mapDocToCover(indexDoc, sfwEnabled = false))
    }

    @Test
    fun parse1996Month_prefersExplicitTitleMonthOverPlaceholderDate() {
        assertEquals(
            10,
            InternetArchiveApi.parse1996Month(
                dateStr = "1996-01-01T00:00:00Z",
                title = "Vogue October 1996",
                identifier = "vogue-1996-10",
            ),
        )
        assertEquals(
            7,
            InternetArchiveApi.parse1996Month(
                dateStr = "1996-07-01T00:00:00Z",
                title = "Wired Issue 4.07",
                identifier = "wired-407",
            ),
        )
        assertEquals(
            0,
            InternetArchiveApi.parse1996Month(
                dateStr = "1996-01-01T00:00:00Z",
                title = "BYTE Volume 21 Number 12",
                identifier = "BYTEVolume21Number12",
            ),
        )
    }
}
