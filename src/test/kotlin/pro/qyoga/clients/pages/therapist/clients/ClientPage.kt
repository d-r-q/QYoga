package pro.qyoga.clients.pages.therapist.clients

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.jsoup.nodes.Element
import pro.qyoga.assertions.PageMatcher
import pro.qyoga.assertions.shouldBeElement
import pro.qyoga.core.clients.internal.Client
import pro.qyoga.infra.html.*
import pro.qyoga.infra.html.Input.Companion.email
import pro.qyoga.infra.html.Input.Companion.tel
import pro.qyoga.infra.html.Input.Companion.text
import java.time.format.DateTimeFormatter

abstract class ClientPage(action: FormAction) : QYogaPage {

    private val birthDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    val clientForm = ClientForm(action)

    override fun match(element: Element) {
        element.select("title").text() shouldMatch title

        element.getElementById(clientForm.id)!! shouldBeElement clientForm
    }

    fun clientForm(client: Client): PageMatcher = object : PageMatcher {

        override fun match(element: Element) {
            element.select(clientForm.firstName.selector()).`val`() shouldBe client.firstName
            element.select(clientForm.lastName.selector()).`val`() shouldBe client.lastName
            element.select(clientForm.middleName.selector()).`val`() shouldBe client.middleName
            element.select(clientForm.birthDate.selector()).`val`() shouldBe client.birthDate.format(birthDateFormat)
            element.select(clientForm.phoneNumber.selector()).`val`() shouldBe client.phoneNumber
            element.select(clientForm.email.selector()).`val`() shouldBe client.email
            element.select(clientForm.address.selector()).`val`() shouldBe client.address
            element.select(clientForm.distributionSource.selector()).text() shouldBe client.distributionSource
            element.select(clientForm.complaints.selector()).text() shouldBe client.complaints
        }

    }

}

val createClientPath = "/therapist/clients/create"

object CreateClientPage : ClientPage(FormAction.classicPost(createClientPath)) {

    override val path = createClientPath

    override val title = "Новый клиент"

}

val editClientPath = "/therapist/clients/{id}"

class EditClientPage(clientId: Long) :
    ClientPage(FormAction.classicPost(editClientPath.replace("{id}", clientId.toString()))) {

    override val path = createClientPath

    override val title = ".*"

}

class ClientForm(action: FormAction) : QYogaForm("createClientForm", action) {

    val firstName = text("firstName")
    val lastName = text("lastName")
    val middleName = text("middleName")
    val birthDate = text("birthDate")
    val phoneNumber = tel("phoneNumber")
    val email = email("email")
    val address = text("address")
    val distributionSource = TextArea("distributionSource")
    val complaints = TextArea("complaints")

    override val components: List<Component> = listOf(
        firstName,
        lastName,
        middleName,
        birthDate,
        phoneNumber,
        email,
        address,
        distributionSource,
        complaints
    )

}