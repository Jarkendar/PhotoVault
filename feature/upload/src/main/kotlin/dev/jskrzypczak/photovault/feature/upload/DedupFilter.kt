package dev.jskrzypczak.photovault.feature.upload

import dev.jskrzypczak.photovault.core.domain.model.UploadFileKey

/**
 * Candidate photo about to be enqueued.
 */
internal data class UploadCandidate(
    val key: UploadFileKey,
    /** Original content URI string, passed unchanged to the worker. */
    val contentUri: String,
    val mimeType: String,
)

/**
 * Result of [partitionCandidates].
 * @property toEnqueue  Candidates that passed all dedup checks and should be enqueued.
 * @property skipped    File names of candidates that were dropped (duplicates).
 */
internal data class PartitionResult(
    val toEnqueue: List<UploadCandidate>,
    val skipped: List<String>,
)

/**
 * Pure function — no side effects, no I/O.
 *
 * Drops a candidate if any of the following is true (checked in order):
 * 1. Its key appears more than once within [candidates] (within-batch duplicate).
 * 2. Its key is already present in [inSessionKeys] (queued/active/done this app session).
 * 3. Its key is listed in [alreadyUploadedKeys] (local ledger from previous sessions).
 */
internal fun partitionCandidates(
    candidates: List<UploadCandidate>,
    inSessionKeys: Set<UploadFileKey>,
    alreadyUploadedKeys: Set<UploadFileKey>,
): PartitionResult {
    val seenInBatch = mutableSetOf<UploadFileKey>()
    val toEnqueue = mutableListOf<UploadCandidate>()
    val skipped = mutableListOf<String>()

    for (candidate in candidates) {
        val key = candidate.key
        when {
            !seenInBatch.add(key) -> skipped.add(key.fileName)  // within-batch dupe
            key in inSessionKeys -> skipped.add(key.fileName)   // already in-session
            key in alreadyUploadedKeys -> skipped.add(key.fileName) // in ledger
            else -> toEnqueue.add(candidate)
        }
    }

    return PartitionResult(toEnqueue, skipped)
}
