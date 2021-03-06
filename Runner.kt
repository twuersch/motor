import layoutengine.AnonymousBlockTile
import layoutengine.Layouter
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

/**
 * Created by timo on 02.01.17.
 */

fun main(args: Array<String>) {
  // Read the example HTML file into a string
  var html: String = U.readFile("test.html")

  // Clean the HTML
  val whitelist = Whitelist
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

  // Parse the HTML into a Document...
  val document = Jsoup.parse(html)

  // ...and do the layouting.
  val screen: AnonymousBlockTile = AnonymousBlockTile(
    width = 360,
    topPadding = 0,
    rightPadding = 0,
    bottomPadding = 0,
    leftPadding = 0,
    parent = null
  )
  Layouter.layout(document, screen)
  for (tile in screen.children) {
    ImageRenderer.render(tile)
    ConsoleRenderer.render(tile)
  }
  System.out.println("done.")
}
