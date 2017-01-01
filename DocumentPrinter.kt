import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.safety.Whitelist

/**
 * Created by timo on 18.11.16.
 */

fun main(args: Array<String>) {
  var html: String = U.readFile("test.html")
  val whitelist: Whitelist = Whitelist
    .relaxed()
    .addTags(
      "header",
      "nav",
      "section",
      "article",
      "aside",
      "footer",
      "form",
      "input",
      "fieldset",
      "title",
      "time",
      "noscript"
    )
    .removeTags(
      "col",
      "colgroup"
    )
  html = Jsoup.clean(html, whitelist)
  val document: Document = Jsoup.parse(html)
  prettyPrintNode(document, 0)
}

fun prettyPrintNode(node: Node, depth: Int) {
  System.out.print(U.repeat("  ", depth))

  if (node is TextNode) {
    if (node.text().matches(Regex("\\s+"))) {
      System.out.println("Whitespace")
    } else {
      System.out.println("Text '" + node.text() +"'")
    }
  } else if (node is Element) {
    if (Layouter.isInlineNode(node)) {
      System.out.println("Inline Element " + node.tagName().toUpperCase())
    } else {
      System.out.println("Block Element " + node.tagName().toUpperCase())
    }
  }

  for (childNode in node.childNodes()) {
    prettyPrintNode(childNode, depth + 1)
  }
}
