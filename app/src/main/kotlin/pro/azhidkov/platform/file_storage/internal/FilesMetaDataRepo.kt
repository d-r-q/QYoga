package pro.azhidkov.platform.file_storage.internal

import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository
import org.springframework.data.mapping.model.BasicPersistentEntity
import org.springframework.data.util.TypeInformation
import org.springframework.stereotype.Repository
import pro.azhidkov.platform.file_storage.api.FileMetaData

@Repository
class FilesMetaDataRepo(
    jdbcAggregateTemplate: JdbcAggregateOperations,
    jdbcConverter: JdbcConverter
) : SimpleJdbcRepository<FileMetaData, Long>(
    jdbcAggregateTemplate,
    BasicPersistentEntity(TypeInformation.of(FileMetaData::class.java)),
    jdbcConverter
)
