package nsu.fit.qyoga.app.questionnaires

import nsu.fit.qyoga.core.clients.api.ClientService
import nsu.fit.qyoga.core.clients.api.Dto.ClientDto
import nsu.fit.qyoga.core.clients.api.Dto.FullNameClientsSearchDto
import nsu.fit.qyoga.core.questionnaires.internal.QuestionnaireCompletionService
import nsu.fit.qyoga.core.users.internal.QyogaUserDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/therapist/questionnaires")
class QuestionnaireGenerateLinkController(
    private val clientService: ClientService,
    private val questionnaireCompletionService: QuestionnaireCompletionService
) {

    /**
     * Получение модального окна генерации ссылки на прохождение
     */
    @GetMapping("/generate-link")
    fun getGenerateLinkModalWindow(
        @ModelAttribute("searchDto") searchDto: FullNameClientsSearchDto,
        @PageableDefault(value = 5, page = 0) pageable: Pageable,
        @ModelAttribute("questionnaireId") questionnaireId: Long,
        model: Model
    ): String {
        model.addAllAttributes(toModelAttributes(
            clientService.getClientsByFullName(
                searchDto,
                pageable
            ),
            searchDto,
            questionnaireId)
        )
        return "questionnaire/generate_link_modal"
    }

    /**
     * Действия с клиентами
     */
    @GetMapping("/generate-link", headers = ["action=true"])
    fun getClientsFiltered(
        @ModelAttribute("searchDto") searchDto: FullNameClientsSearchDto,
        @PageableDefault(value = 5, page = 0) pageable: Pageable,
        @ModelAttribute("questionnaireId") questionnaireId: Long,
        model: Model
    ): String {
        model.addAllAttributes(toModelAttributes(
            clientService.getClientsByFullName(
                searchDto,
                pageable
            ),
            searchDto,
            questionnaireId)
        )
        return "questionnaire/generate_link_modal :: clients"
    }

    /**
     * Генерация ссылки на прохождение
     */
    @PostMapping("/generate-link")
    fun generateLink(
        @ModelAttribute("questionnaireId") questionnaireId: Long,
        @ModelAttribute("clientId") clientId: Long,
        @AuthenticationPrincipal principal: QyogaUserDetails,
        model: Model
    ): String {
        model.addAttribute(
            "generatedLink",
            questionnaireCompletionService.generateCompletingLink(questionnaireId, clientId, principal.id)
        )
        return "questionnaire/generate_link_modal :: questionnaire-url"
    }

    fun toModelAttributes(
        clients: Page<ClientDto>,
        searchDto: FullNameClientsSearchDto,
        questionnaireId: Long
    ): Map<String, *> = mapOf(
        "searchDto" to searchDto,
        "clients" to clients,
        "questionnaireId" to questionnaireId
    )
}
