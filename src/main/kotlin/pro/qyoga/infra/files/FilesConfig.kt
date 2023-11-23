package pro.qyoga.infra.files

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import pro.qyoga.infra.db.SdjConfig

@Import(SdjConfig::class)
@ComponentScan
@Configuration
class FilesConfig