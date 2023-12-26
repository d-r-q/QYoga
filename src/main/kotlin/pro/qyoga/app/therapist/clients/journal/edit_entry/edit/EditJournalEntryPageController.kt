package pro.qyoga.app.therapist.clients.journal.edit_entry.edit

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import pro.qyoga.app.therapist.clients.journal.edit_entry.shared.JOURNAL_ENTRY_VIEW_NAME
import pro.qyoga.core.clients.journals.api.DuplicatedDate
import pro.qyoga.core.clients.journals.api.JournalsService
import pro.qyoga.core.users.internal.QyogaUserDetails
import pro.qyoga.platform.kotlin.isFailureOf
import pro.qyoga.platform.spring.http.hxRedirect
import pro.qyoga.platform.spring.mvc.modelAndView


@Controller
@RequestMapping("/therapist/clients/{clientId}/journal/{entryId}")
class EditJournalEntryPageController(
    private val journalsService: JournalsService,
    private val getJournalEntryWorkflow: GetJournalEntryWorkflow,
    private val editJournalEntryWorkflow: EditJournalEntryWorkflow,
) {

    @GetMapping()
    fun getEditJournalEntryPage(
        @PathVariable clientId: Long,
        @PathVariable entryId: Long
    ): ModelAndView {
        val result = getJournalEntryWorkflow.getJournalEntry(clientId, entryId)

        return modelAndView(JOURNAL_ENTRY_VIEW_NAME) {
            "client" bindTo result.client
            "entry" bindTo result
            "formAction" bindTo editFormAction(clientId, entryId)
        }
    }

    private fun editFormAction(clientId: Long, entryId: Long) = "/therapist/clients/$clientId/journal/$entryId"

    @PostMapping
    fun editJournalEntry(
        @PathVariable clientId: Long,
        @PathVariable entryId: Long,
        @ModelAttribute editJournalEntryRequest: EditJournalEntryRequest,
        @AuthenticationPrincipal principal: QyogaUserDetails,
    ): Any {
        val result = runCatching {
            editJournalEntryWorkflow.editJournalEntry(clientId, entryId, editJournalEntryRequest, principal)
        }

        return when {
            result.isSuccess ->
                hxRedirect("/therapist/clients/$clientId/journal")

            result.isFailureOf<DuplicatedDate>() -> {
                val ex = result.exceptionOrNull() as DuplicatedDate
                modelAndView("$JOURNAL_ENTRY_VIEW_NAME :: journalEntryFrom") {
                    "client" bindTo ex.duplicatedEntry.client
                    "entry" bindTo ex.duplicatedEntry
                    "entryDate" bindTo ex.duplicatedEntry.date
                    "duplicatedDate" bindTo true
                    "formAction" bindTo editFormAction(clientId, entryId)
                }
            }

            else ->
                result.getOrThrow()
        }
    }

    @DeleteMapping
    fun deleteJournalEntry(
        @PathVariable clientId: Long,
        @PathVariable entryId: Long,
    ): ResponseEntity<Unit> {
        val result = runCatching {
            journalsService.deleteEntry(clientId, entryId)
        }

        result.onFailure {
            throw it
        }

        return ResponseEntity.ok()
            .build()
    }

}