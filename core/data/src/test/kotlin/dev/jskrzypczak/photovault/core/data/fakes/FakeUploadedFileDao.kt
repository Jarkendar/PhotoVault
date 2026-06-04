package dev.jskrzypczak.photovault.core.data.fakes

import dev.jskrzypczak.photovault.core.database.dao.UploadedFileDao
import dev.jskrzypczak.photovault.core.database.entity.UploadedFileEntity

class FakeUploadedFileDao : UploadedFileDao {

    private val records = mutableListOf<UploadedFileEntity>()

    override suspend fun insert(entity: UploadedFileEntity) {
        records.removeAll { it.fileName == entity.fileName && it.sizeBytes == entity.sizeBytes }
        records.add(entity)
    }

    override suspend fun exists(fileName: String, sizeBytes: Long): Boolean =
        records.any { it.fileName == fileName && it.sizeBytes == sizeBytes }
}
