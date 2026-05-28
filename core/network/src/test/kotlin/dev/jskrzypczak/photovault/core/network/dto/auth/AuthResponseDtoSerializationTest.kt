package dev.jarkendar.photovault.core.network.dto.auth

import dev.jarkendar.photovault.core.network.dto.user.UserDto
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthResponseDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `parses login response with all tokens and user`() {
        val parsed = json.decodeFromString<AuthResponseDto>(
            """
            {
              "accessToken":"acc-abc",
              "refreshToken":"ref-xyz",
              "user":{"id":"user-jarek","username":"jarek","displayName":"Jarek"}
            }
            """.trimIndent(),
        )
        assertEquals("acc-abc", parsed.accessToken)
        assertEquals("ref-xyz", parsed.refreshToken)
        assertEquals(UserDto("user-jarek", "jarek", "Jarek"), parsed.user)
    }

    @Test
    fun `parses RefreshRequestDto`() {
        val parsed = json.decodeFromString<RefreshRequestDto>("""{"refreshToken":"ref-xyz"}""")
        assertEquals("ref-xyz", parsed.refreshToken)
    }

    @Test
    fun `serializes LoginRequestDto`() {
        val request = LoginRequestDto(username = "jarek", password = "secret")
        val serialized = json.encodeToString(LoginRequestDto.serializer(), request)
        assertEquals(
            Json.parseToJsonElement("""{"username":"jarek","password":"secret"}"""),
            Json.parseToJsonElement(serialized),
        )
    }
}