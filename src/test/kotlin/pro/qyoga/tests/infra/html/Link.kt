package pro.qyoga.tests.infra.html

import io.kotest.assertions.withClue
import io.kotest.matchers.string.shouldMatch
import org.jsoup.nodes.Element

class Link(
    val id: String,
    url: String,
    val text: String,
    val targetAttr: String = "href"
) : Component {

    val urlRegex = url.replace("\\{.*}".toRegex(), ".*")

    constructor(id: String, page: QYogaPage, text: String) : this(id, page.path, text)

    override fun selector(): String =
        "a#$id:contains($text)"

    override fun match(element: Element) {
        withClue("Invalid link URL") {
            element.attr(targetAttr) shouldMatch urlRegex
        }
    }

    companion object {

        fun hxGet(id: String, url: String, text: String) = Link(id, url, text, "hx-get")

    }

}