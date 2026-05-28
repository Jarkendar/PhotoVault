package dev.jarkendar.photovault.core.network.dto.user

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class UserDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    @Test
    fun `parses full user`() {
        val parsed = json.decodeFromString<UserDto>(
            """{"id":"user-jarek","username":"jarek","displayName":"Jarek"}""",
        )
        assertEquals(UserDto("user-jarek", "jarek", "Jarek"), parsed)
    }

    @Test
    fun `parses UserRefDto without username`() {
        val parsed = json.decodeFromString<UserRefDto>(
            """{"id":"user-jarek","displayName":"Jarek"}""",
        )
        assertEquals(UserRefDto("user-jarek", "Jarek"), parsed)
    }

    @Test
    fun `parses UserListDto`() {
        val parsed = json.decodeFromString<UserListDto>(
            """{"items":[{"id":"u1","username":"a","displayName":"A"}]}""",
        )
        assertEquals(1, parsed.items.size)
    }
}