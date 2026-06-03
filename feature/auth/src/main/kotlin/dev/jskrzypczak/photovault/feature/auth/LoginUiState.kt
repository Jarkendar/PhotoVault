package dev.jskrzypczak.photovault.feature.auth

enum class LoginError {
    INVALID_CREDENTIALS,
    NETWORK,
    UNKNOWN,
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isSubmitting: Boolean = false,
    val error: LoginError? = null,
)
