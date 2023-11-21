package pro.qyoga.core.clients.internal

import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository
import org.springframework.data.mapping.model.BasicPersistentEntity
import org.springframework.data.util.TypeInformation
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import pro.qyoga.core.clients.api.ClientDto
import pro.qyoga.core.clients.api.ClientSearchDto
import pro.qyoga.core.clients.api.ClientsCrudService
import pro.qyoga.platform.spring.sdj.example
import pro.qyoga.platform.spring.sdj.probeFrom
import pro.qyoga.platform.spring.sdj.sortBy

@Repository
class ClientRepo(
    private val jdbcAggregateTemplate: JdbcAggregateOperations,
    jdbcConverter: JdbcConverter
) : SimpleJdbcRepository<Client, Long>(
    jdbcAggregateTemplate,
    BasicPersistentEntity(TypeInformation.of(Client::class.java)),
    jdbcConverter
), ClientsCrudService {

    @Transactional
    override fun saveAll(clientDtos: List<ClientDto>): List<ClientDto> {
        return jdbcAggregateTemplate.saveAll(clientDtos.map { Client(it) }).map { it.toDto() }
    }

    override fun findClients(
        searchDto: ClientSearchDto,
        page: Pageable
    ): Page<ClientDto> {
        val example = example<Client>(probeFrom(searchDto)) {
            withMatcher(searchDto::firstName, ExampleMatcher.GenericPropertyMatcher().contains().ignoreCase())
            withMatcher(searchDto::lastName, ExampleMatcher.GenericPropertyMatcher().contains().ignoreCase())
            withMatcher(searchDto::patronymic, ExampleMatcher.GenericPropertyMatcher().contains().ignoreCase())
            withMatcher(searchDto::phoneNumber, ExampleMatcher.GenericPropertyMatcher().contains().ignoreCase())
        }

        val pageRequest = PageRequest.of(page.pageNumber, page.pageSize, sortBy(Client::lastName))

        return findAll(example, pageRequest).map {
            ClientDto(it.firstName, it.lastName, it.patronymic, it.birthDate, it.phoneNumber, it.email, it.id)
        }
    }

    @Transactional
    override fun deleteById(id: Long) {
        super.deleteById(id)
    }

}
