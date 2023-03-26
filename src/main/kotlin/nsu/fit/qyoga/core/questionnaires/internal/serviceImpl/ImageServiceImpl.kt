package nsu.fit.qyoga.core.questionnaires.internal.serviceImpl

import nsu.fit.qyoga.core.questionnaires.api.model.Image
import nsu.fit.qyoga.core.questionnaires.api.services.ImageService
import nsu.fit.qyoga.core.questionnaires.internal.repository.ImageJdbcTemplateRepo
import org.springframework.stereotype.Service

@Service
class ImageServiceImpl(
    private val imageRepo: ImageJdbcTemplateRepo
) :ImageService {
    override fun uploadImage(image: Image): Long {
        return imageRepo.save(
            Image(
                name = image.name,
                mediaType = image.mediaType,
                size = image.size,
                data = image.data
            )
        )
    }

    override fun getImage(id: Long): Image? {
        return imageRepo.findById(id)
    }
}