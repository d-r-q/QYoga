package nsu.fit.qyoga.cases.core.questionnaires.ui

import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import nsu.fit.qyoga.infra.QYogaAppTestBase
import nsu.fit.qyoga.infra.db.DbInitializer
import org.jsoup.Jsoup
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class QuestionViewTest : QYogaAppTestBase() {
    @Autowired
    lateinit var dbInitializer: DbInitializer

    @BeforeEach
    fun setupDb() {
        dbInitializer.executeScripts(
            "db/questionnaires/questionnaires-init-script.sql" to "dataSource",
            "db/questionnaires/questionnaires-insert-questionnaire.sql" to "dataSource"
        )
    }

    @Test
    fun `QYoga returns questionnaire view page`() {
        Given {
            authorized()
        } When {
            get("/therapist/questionnaires/1")
        } Then {
            val body = Jsoup.parse(extract().body().asString())
            io.github.ulfs.assertj.jsoup.Assertions.assertThatSpec(body) {
                node("#questions") { exists() }

                node("#question0") { exists() }
                node("#question0Title") {
                    attribute("value") { hasText("questionSINGLE") }
                }
                node("#question0Answer0") {
                    attribute("value") { hasText("questionSINGLEAnswer") }
                }

                node("#question1") { exists() }
                node("#question1Title") {
                    attribute("value") { hasText("questionSEVERAL") }
                }
                node("#question1Answer0") {
                    attribute("value") { hasText("questionSEVERALAnswer") }
                }

                node("#question2") { exists() }
                node("#question2Title") {
                    attribute("value") { hasText("questionRANGE") }
                }
                node("#question2Answer0LowerBound") {
                    attribute("value") { hasText("2") }
                }
                node("#question2Answer0LowerBoundText") {
                    attribute("value") { hasText("lower_bound_text") }
                }
                node("#question2Answer0UpperBound") {
                    attribute("value") { hasText("7") }
                }
                node("#question2Answer0UpperBoundText") {
                    attribute("value") { hasText("upper_bound_text") }
                }

                node("#question3") { exists() }
                node("#question3Title") {
                    attribute("value") { hasText("questionTEXT") }
                }

                node("#watch-key-btn") {
                    exists()
                    attribute("action") { hasText("/therapist/questionnaires/1/decoding") }
                    containsText("Посмотреть ключи")
                }
                node("#end-watching-btn") {
                    exists()
                    attribute("action") { hasText("/therapist/questionnaires") }
                    containsText("Закончить просмотр")
                }
            }
        }
    }

    @Test
    fun `QYoga returns questionnaire view result page`() {
        Given {
            authorized()
        } When {
            get("/therapist/questionnaires/1/decoding")
        } Then {
            val body = Jsoup.parse(extract().body().asString())
            io.github.ulfs.assertj.jsoup.Assertions.assertThatSpec(body) {
                node("#card-body") { exists() }

                node("#decoding0LowerBound") {
                    exists()
                    attribute("value") { hasText("10") }
                }
                node("#decoding0UpperBound") {
                    exists()
                    attribute("value") { hasText("20") }
                }
                node("#decoding0Result") {
                    exists()
                    attribute("value") { hasText("test1") }
                }

                node("#decoding1LowerBound") {
                    exists()
                    attribute("value") { hasText("20") }
                }
                node("#decoding1UpperBound") {
                    exists()
                    attribute("value") { hasText("30") }
                }
                node("#decoding1Result") {
                    exists()
                    attribute("value") { hasText("test2") }
                }

                node("#decoding2LowerBound") {
                    exists()
                    attribute("value") { hasText("30") }
                }
                node("#decoding2UpperBound") {
                    exists()
                    attribute("value") { hasText("40") }
                }
                node("#decoding2Result") {
                    exists()
                    attribute("value") { hasText("test3") }
                }

                node("#watch-questionnaire-btn") {
                    exists()
                    attribute("action") { hasText("/therapist/questionnaires/1") }
                    containsText("Опросник")
                }
                node("#end-watching-btn") {
                    exists()
                    attribute("action") { hasText("/therapist/questionnaires") }
                    containsText("Закончить просмотр")
                }
            }
        }
    }
}