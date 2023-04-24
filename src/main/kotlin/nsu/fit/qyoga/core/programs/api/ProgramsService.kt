package nsu.fit.qyoga.core.programs.api

import nsu.fit.qyoga.core.programs.api.dtos.ProgramDto
import nsu.fit.qyoga.core.programs.api.dtos.ProgramSearchDto
import nsu.fit.qyoga.core.programs.api.dtos.ProgramWithExercisesDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProgramsService {
    fun getPrograms(
        searchDto: ProgramSearchDto,
        pageable: Pageable
    ): Page<ProgramDto>

    fun getProgramById(id: Long): ProgramWithExercisesDto

    fun downloadProgram(program: ProgramWithExercisesDto)
}