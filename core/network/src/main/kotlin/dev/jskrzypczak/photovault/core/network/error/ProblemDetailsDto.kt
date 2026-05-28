package dev.jarkendar.photovault.core.network.error

import kotlinx.serialization.Serializable

@Serializable
data class ProblemDetailsDto(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String? = null,
    val instance: String? = null,
    val errors: Map<String, List<String>>? = null,
)