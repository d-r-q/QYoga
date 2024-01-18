package pro.qyoga.core.clients.journals.internal

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jdbc.core.JdbcAggregateOperations
import org.springframework.data.jdbc.core.convert.JdbcConverter
import org.springframework.data.jdbc.core.mapping.AggregateReference
import org.springframework.data.mapping.model.BasicPersistentEntity
import org.springframework.data.util.TypeInformation
import org.springframework.stereotype.Repository
import pro.qyoga.core.clients.journals.api.JournalEntry
import pro.qyoga.core.clients.journals.api.JournalPageRequest
import pro.qyoga.platform.spring.sdj.erpo.ErgoRepository
import pro.qyoga.platform.spring.sdj.sortBy
import kotlin.reflect.KProperty1


@Repository
class JournalEntriesRepo(
    override val jdbcAggregateTemplate: JdbcAggregateOperations,
    jdbcConverter: JdbcConverter
) : ErgoRepository<JournalEntry, Long>(
    jdbcAggregateTemplate,
    BasicPersistentEntity(TypeInformation.of(JournalEntry::class.java)),
    jdbcConverter
) {

    fun getJournalPage(journalPageRequest: JournalPageRequest): Page<JournalEntry> {
        return findAll(
            pageRequest = PageRequest.of(0, journalPageRequest.pageSize, sortBy(JournalEntry::date).descending()),
            fetch = journalPageRequest.fetch,
        ) {
            JournalEntry::client isEqual AggregateReference.to(journalPageRequest.clientId)
            JournalEntry::date isLessThanIfNotNull journalPageRequest.date
        }
    }

    fun getEntry(
        clientId: Long,
        entryId: Long,
        fetch: Iterable<KProperty1<JournalEntry, *>> = emptySet()
    ): JournalEntry? {
        return findOne(fetch) {
            JournalEntry::client isEqual AggregateReference.to(clientId)
            JournalEntry::id isEqual entryId
        }
    }

}