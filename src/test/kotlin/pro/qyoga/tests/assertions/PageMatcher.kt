package pro.qyoga.tests.assertions

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import pro.qyoga.tests.infra.html.Component


@FunctionalInterface
interface PageMatcher {

    fun match(element: Element)

}

infix fun Document.shouldBe(pageMatcher: PageMatcher) = pageMatcher.match(this)

infix fun Document.shouldBePage(pageMatcher: PageMatcher) = pageMatcher.match(this)

infix fun ByteArray.shouldBePage(pageMatcher: PageMatcher) = pageMatcher.match(Jsoup.parse(String(this)))

infix fun Element.shouldBeElement(pageMatcher: PageMatcher) = pageMatcher.match(this)

infix fun Element.shouldHaveComponent(component: Component) {
    val element = this.select(component.selector())
    withClue("Cannot find component ${component.name} by selector ${component.selector()} in $this") {
        element shouldHaveSize 1
    }
    component.match(element[0])
}

infix fun Element.shouldHave(pageMatcher: PageMatcher) {
    pageMatcher.match(this)
}

infix fun Document.shouldHave(pageMatcher: PageMatcher) = pageMatcher.match(this)

infix fun Element.shouldNotHave(component: Component) {
    val element = this.select(component.selector())
    withClue("Component ${component.name} is found by selector ${component.selector()} in $this") {
        element shouldHaveSize 0
    }
}

infix fun Element.shouldNotHave(selector: String) {
    val element = this.select(selector)
    withClue("Tag is found by selector $selector") {
        element shouldHaveSize 0
    }
}

infix fun Element.shouldHaveAttr(pageMatcher: PageMatcher) = pageMatcher.match(this)