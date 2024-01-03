package pro.qyoga.core.therapy

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import pro.qyoga.infra.db.SdjConfig
import pro.qyoga.platform.file_storage.ImagesConfig

@Import(SdjConfig::class, ImagesConfig::class)
@ComponentScan
@Configuration
class TherapyConfig