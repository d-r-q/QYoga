package nsu.fit.qyoga.core.completingQuestionnaires.internal.repository

import nsu.fit.platform.errors.ResourceNotFound
import nsu.fit.platform.spring.queryForPage
import nsu.fit.qyoga.core.completingQuestionnaires.api.dtos.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class CompletingJdbcTemplateRepo(
    private val jdbcTemplate: NamedParameterJdbcOperations
) {
    fun findQuestionnaireCompletingById(
        therapistId: Long,
        completingSearchDto: CompletingSearchDto,
        pageable: Pageable
    ): Page<CompletingDto> {
        val params = mapOf<String, Any>(
            "therapistId" to therapistId,
            "name" to completingSearchDto.clientName,
            "qTitle" to completingSearchDto.title
        )
        val pageableSortType = pageable.sort.toString().substringAfter(": ")
        if (pageableSortType != "ASC" && pageableSortType != "DESC") {
            throw ResourceNotFound("Ошибка типа сортировки")
        }
        return jdbcTemplate.queryForPage(
            getQueryBySortType(pageableSortType),
            params,
            pageable
        ) { rs: ResultSet, _: Int ->
            CompletingDto(
                rs.getLong("completingId"),
                CompletingQuestionnaireDto(
                    rs.getLong("questionnaireId"),
                    rs.getString("questionnaireTitle")
                ),
                CompletingClientDto(
                    rs.getLong("clientId"),
                    rs.getString("clientFirstName"),
                    rs.getString("clientLastName"),
                    rs.getString("clientPatronymic")
                ),
                rs.getDate("completingDate"),
                rs.getLong("numericResult"),
                rs.getString("textResult")
            )
        }
    }

    fun getQueryBySortType(type: String): String {
        return """
            SELECT
            clients.id AS clientId,
            clients.first_name AS clientFirstName,
            clients.last_name AS clientLastName,
            clients.patronymic as clientPatronymic,
            completing.id AS completingId,
            completing.completing_date AS completingDate,
            completing.numeric_result AS numericResult,
            completing.text_result AS textResult,
            questionnaires.id AS questionnaireId,
            questionnaires.title AS questionnaireTitle
            FROM completing
            LEFT JOIN clients ON clients.id = completing.client_id
            LEFT JOIN questionnaires ON questionnaires.id = completing.questionnaire_id
            WHERE completing.therapist_id = :therapistId
            AND clients.first_name||''||clients.last_name||''||clients.patronymic LIKE '%' || :name || '%'
            AND questionnaires.title LIKE '%' || :qTitle || '%'
            ORDER BY completingDate ${if (type == "UNSORTED") "ASC" else type}
        """.trimIndent()
    }
}