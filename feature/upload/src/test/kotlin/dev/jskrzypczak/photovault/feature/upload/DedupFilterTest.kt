package dev.jskrzypczak.photovault.feature.upload

import dev.jskrzypczak.photovault.core.domain.model.UploadFileKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun candidate(name: String, size: Long = 1_000L, uri: String = "content://$name") =
    UploadCandidate(UploadFileKey(name, size), uri, "image/jpeg")

class DedupFilterTest {

    @Test
    fun `all new candidates are enqueued with empty ledger and empty session`() {
        val candidates = listOf(candidate("A.jpg"), candidate("B.jpg"))

        val result = partitionCandidates(candidates, emptySet(), emptySet())

        assertEquals(2, result.toEnqueue.size)
        assertTrue(result.skipped.isEmpty())
    }

    @Test
    fun `within-batch duplicate is skipped, first occurrence enqueued`() {
        val first = candidate("A.jpg")
        val dup = candidate("A.jpg")

        val result = partitionCandidates(listOf(first, dup), emptySet(), emptySet())

        assertEquals(1, result.toEnqueue.size)
        assertEquals(first, result.toEnqueue[0])
        assertEquals(listOf("A.jpg"), result.skipped)
    }

    @Test
    fun `key already in session is skipped`() {
        val inSession = setOf(UploadFileKey("A.jpg", 1_000L))
        val candidates = listOf(candidate("A.jpg"), candidate("B.jpg"))

        val result = partitionCandidates(candidates, inSession, emptySet())

        assertEquals(1, result.toEnqueue.size)
        assertEquals("B.jpg", result.toEnqueue[0].key.fileName)
        assertEquals(listOf("A.jpg"), result.skipped)
    }

    @Test
    fun `key in ledger is skipped`() {
        val ledger = setOf(UploadFileKey("A.jpg", 1_000L))
        val candidates = listOf(candidate("A.jpg"), candidate("B.jpg"))

        val result = partitionCandidates(candidates, emptySet(), ledger)

        assertEquals(1, result.toEnqueue.size)
        assertEquals("B.jpg", result.toEnqueue[0].key.fileName)
        assertEquals(listOf("A.jpg"), result.skipped)
    }

    @Test
    fun `same name but different size is NOT a duplicate`() {
        val ledger = setOf(UploadFileKey("A.jpg", 1_000L))
        val candidates = listOf(candidate("A.jpg", size = 2_000L))

        val result = partitionCandidates(candidates, emptySet(), ledger)

        assertEquals(1, result.toEnqueue.size)
        assertTrue(result.skipped.isEmpty())
    }

    @Test
    fun `all three dedup sources combined`() {
        val a = candidate("A.jpg")          // first occurrence enqueued, second (aDup) skipped
        val aDup = candidate("A.jpg")
        val b = candidate("B.jpg")          // in-session → skipped
        val c = candidate("C.jpg")          // in ledger → skipped
        val d = candidate("D.jpg")          // new → enqueued

        val result = partitionCandidates(
            candidates = listOf(a, aDup, b, c, d),
            inSessionKeys = setOf(UploadFileKey("B.jpg", 1_000L)),
            alreadyUploadedKeys = setOf(UploadFileKey("C.jpg", 1_000L)),
        )

        // a (first A.jpg) + d are unique; aDup, b, c are dropped
        assertEquals(listOf(a, d), result.toEnqueue)
        assertEquals(setOf("A.jpg", "B.jpg", "C.jpg"), result.skipped.toSet())
    }

    @Test
    fun `empty candidates list returns empty result`() {
        val result = partitionCandidates(emptyList(), emptySet(), emptySet())

        assertTrue(result.toEnqueue.isEmpty())
        assertTrue(result.skipped.isEmpty())
    }
}
