package pro.qyoga.core.therapy.exercises

import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.transaction.support.TransactionTemplate
import pro.qyoga.platform.file_storage.FilesStorageConfig
import pro.qyoga.platform.file_storage.internal.FilesMetaDataRepo
import pro.qyoga.platform.file_storage.internal.MinioFilesStorage


@Import(FilesStorageConfig::class)
@Configuration
class ExercisesConfig(
    private val filesMetaDataRepo: FilesMetaDataRepo,
    private val minioClient: MinioClient,
    private val transactionTemplate: TransactionTemplate
) {

    @Bean
    fun exerciseStepsImagesStorage() =
        MinioFilesStorage(filesMetaDataRepo, minioClient, "exercise.steps")

}