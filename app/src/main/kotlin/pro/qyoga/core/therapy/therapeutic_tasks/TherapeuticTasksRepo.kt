package pro.qyoga.core.therapy.therapeutic_tasks

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.mapping.model.BasicPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalMappingContext
import org.springframework.data.util.TypeInformation
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import pro.azhidkov.platform.spring.sdj.erpo.ErgoRepository
import pro.azhidkov.platform.spring.sdj.withSortBy
import pro.qyoga.core.therapy.therapeutic_tasks.errors.DuplicatedTherapeuticTaskName
import pro.qyoga.core.therapy.therapeutic_tasks.model.TherapeuticTask
import java.sql.Timestamp


@Repository
class TherapeuticTasksRepo(
    jdbcAggregateTemplate: JdbcAggregateOperations,
    namedParameterJdbcOperations: NamedParameterJdbcOperations,
    jdbcConverter: JdbcConverter,
    relationalMappingContext: RelationalMappingContext,
) : ErgoRepository<TherapeuticTask, Long>(
    jdbcAggregateTemplate,
    namedParameterJdbcOperations,
    BasicPersistentEntity(TypeInformation.of(TherapeuticTask::class.java)),
    jdbcConverter,
    relationalMappingContext
) {

    object Page {
        val topFiveByName = Pageable.ofSize(5).withSortBy(TherapeuticTask::name)
        val topTenByName = Pageable.ofSize(10).withSortBy(TherapeuticTask::name)
    }

    @Transactional
    override fun <S : TherapeuticTask?> save(instance: S & Any): S & Any {
        return saveAndMapDuplicatedKeyException(instance) { ex ->
            DuplicatedTherapeuticTaskName(instance, ex)
        }
    }

    fun getOrCreate(therapeuticTask: TherapeuticTask): TherapeuticTask {
        val id = upsert(
            """
            INSERT INTO therapeutic_tasks (name, owner, created_at, version) VALUES (:name, :owner, :createdAt, 1)
            ON CONFLICT (owner, lower(name)) DO UPDATE SET name = excluded.name
            RETURNING id
        """,
            "name" to therapeuticTask.name,
            "owner" to therapeuticTask.owner.id,
            "createdAt" to Timestamp(therapeuticTask.createdAt.toEpochMilli())
        )

        return therapeuticTask.copy(id = id)
    }

}

fun TherapeuticTasksRepo.findTherapistTasksSliceByName(
    therapistId: Long,
    searchKey: String?,
    page: Pageable
): Slice<TherapeuticTask> {
    return findPage(page) {
        TherapeuticTask::owner isEqual therapistId
        TherapeuticTask::name isILikeIfNotNull searchKey
    }
}

fun TherapeuticTasksRepo.findOneByName(name: String): TherapeuticTask? =
    findOne {
        TherapeuticTask::name isEqual name
    }
