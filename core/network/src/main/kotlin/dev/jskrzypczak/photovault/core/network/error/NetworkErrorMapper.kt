package dev.jarkendar.photovault.core.network.error

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException

private val problemJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    isLenient = true
}

private const val UNKNOWN_PROBLEM_TYPE = "about:blank"

suspend fun mapToNetworkError(response: HttpResponse): NetworkError {
    val body = runCatching { response.bodyAsText() }.getOrDefault("")
    val problem = parseProblem(body)
    val type = problem?.type ?: UNKNOWN_PROBLEM_TYPE
    val detail = problem?.detail
    val status = response.status.value
    return when (status) {
        401 -> NetworkError.Unauthenticated(type)
        403 -> NetworkError.Forbidden(type)
        404 -> NetworkError.NotFound(type)
        409 -> NetworkError.Conflict(type, detail)
        400 -> if (type.contains("validation-failed")) {
            NetworkError.ValidationFailed(type, problem?.errors.orEmpty())
        } else {
            NetworkError.ServerError(status, type, detail)
        }
        in 400..599 -> NetworkError.ServerError(status, type, detail)
        else -> NetworkError.ServerError(status, type, detail)
    }
}

fun mapToNetworkError(throwable: Throwable): NetworkError = when (throwable) {
    is NetworkError -> throwable
    is HttpRequestTimeoutException -> NetworkError.Timeout
    is IOException -> NetworkError.NoConnectivity
    is SerializationException -> NetworkError.Unknown(throwable)
    else -> NetworkError.Unknown(throwable)
}

private fun parseProblem(body: String): ProblemDetailsDto? {
    if (body.isBlank()) return null
    return runCatching { problemJson.decodeFromString<ProblemDetailsDto>(body) }.getOrNull()
}
