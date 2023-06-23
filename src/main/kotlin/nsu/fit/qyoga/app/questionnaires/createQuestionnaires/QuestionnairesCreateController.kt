package nsu.fit.qyoga.app.questionnaires.createQuestionnaires

import jakarta.servlet.http.HttpSession
import nsu.fit.qyoga.core.questionnaires.api.dtos.*
import nsu.fit.qyoga.core.questionnaires.api.errors.QuestionnaireException
import nsu.fit.qyoga.core.questionnaires.api.services.QuestionnaireService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/therapist/questionnaires")
class QuestionnairesCreateController(
    private val questionnaireService: QuestionnaireService,
    private val httpSession: HttpSession
) {

    /**
     * Получение страницы редактирования
     */
    @GetMapping("/edit")
    fun getCreateQuestionnairePage(): String {
        val questionnaire = getQuestionnaireFromSession()
        if (questionnaire == null) {
            setQuestionnaireInSession(
                CreateQuestionnaireDto(
                    question = mutableListOf(CreateQuestionDto(answers = listOf(CreateAnswerDto()))),
                    decoding = mutableListOf()
                )
            )
        }
        return "questionnaire/create-questionnaire"
    }

    /**
     * Создание нового опросника
     */
    @GetMapping("/new")
    fun createQuestionnaire(): String {
        setQuestionnaireInSession(
            CreateQuestionnaireDto(
                question = mutableListOf(CreateQuestionDto(answers = listOf(CreateAnswerDto()))),
                decoding = mutableListOf(DecodingDto())
            )
        )
        return "redirect:/therapist/questionnaires/edit"
    }

    /**
     * Редактирование опросника
     */
    @GetMapping("/{id}/edit")
    fun editQuestionnaire(
        @PathVariable id: Long
    ): String {
        setQuestionnaireInSession(questionnaireService.findQuestionnaireWithQuestions(id))
        return "redirect:/therapist/questionnaires/edit"
    }

    /**
     * Создание опросника
     */
    @PostMapping("/edit")
    fun createQuestionnaire(
        @ModelAttribute("questionnaire") questionnaire: CreateQuestionnaireDto,
    ): String {
        val sQuestionnaire = getQuestionnaireFromSession()
            ?: throw QuestionnaireException("Ошибка извлечения опросника из сессии")
        setQuestionnaireInSession(sQuestionnaire.copy(title = questionnaire.title, question = questionnaire.question))
        return "redirect:/therapist/questionnaires/edit/setResult"
    }


    /**
     * Задание заголовка опросника
     */
    @PostMapping("/edit/title")
    @ResponseBody
    fun changeQuestionnaireTitle(
        @RequestParam title: String
    ): HttpStatus {
        val questionnaire = getQuestionnaireFromSession()
            ?: throw QuestionnaireException("Ошибка извлечения опросника из сессии")
        setQuestionnaireInSession(questionnaire.copy(title = title))
        return HttpStatus.OK
    }

    fun getQuestionnaireFromSession(): CreateQuestionnaireDto? {
        return httpSession.getAttribute("questionnaire") as CreateQuestionnaireDto?
    }

    fun setQuestionnaireInSession(questionnaire: CreateQuestionnaireDto) {
        httpSession.setAttribute("questionnaire", questionnaire)
    }
}
