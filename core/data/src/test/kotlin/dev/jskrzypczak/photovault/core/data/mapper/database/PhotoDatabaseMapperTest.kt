package dev.jarkendar.photovault.core.data.mapper.database

import dev.jarkendar.photovault.core.data.fixtures.PhotoTestFixtures
import dev.jarkendar.photovault.core.domain.model.GeoLocation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PhotoDatabaseMapperTest {

    @Test
    fun `PhotoWithRelations toDomain maps all fields`() {
        val domain = PhotoTestFixtures.PHOTO_WITH_RELATIONS.toDomain()
        assertEquals(PhotoTestFixtures.PHOTO_DOMAIN, domain)
    }

    @Test
    fun `toDomain with null geo fields returns null location`() {
        val domain = PhotoTestFixtures.PHOTO_WITH_RELATIONS_NO_OPTIONALS.toDomain()
        assertNull(domain.location)
    }

    @Test
    fun `toDomain with non-null geo fields returns populated location`() {
        val domain = PhotoTestFixtures.PHOTO_WITH_RELATIONS.toDomain()
        val location = assertNotNull(domain.location)
        assertEquals(GeoLocation(latitude = 54.4641, longitude = 18.5734, placeName = "Sopot, PL"), location)
    }

    @Test
    fun `toDomain with empty relations gives empty domain lists`() {
        val domain = PhotoTestFixtures.PHOTO_WITH_RELATIONS_NO_OPTIONALS.toDomain()
        assertEquals(emptyList(), domain.tags)
        assertEquals(emptyList(), domain.categories)
        assertEquals(emptyList(), domain.labels)
    }

    @Test
    fun `Photo toEntity round-trip preserves data`() {
        val entity = PhotoTestFixtures.PHOTO_DOMAIN.toEntity()
        assertEquals(PhotoTestFixtures.PHOTO_ENTITY, entity)
    }

    @Test
    fun `Photo toEntity with null location flattens all geo fields to null`() {
        val domainNoLocation = PhotoTestFixtures.PHOTO_DOMAIN.copy(location = null)
        val entity = domainNoLocation.toEntity()
        assertNull(entity.latitude)
        assertNull(entity.longitude)
        assertNull(entity.placeName)
    }
}