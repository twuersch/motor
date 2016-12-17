import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Created by timo on 04.12.16.
 */
class InlinePart (
  x: Int = 0,
  y: Int = 0,
  width: Int = 0,
  height: Int = 0,
  topPadding: Int = 5,
  rightPadding: Int = 5,
  bottomPadding: Int = 5,
  leftPadding: Int = 5,
  children: MutableList<Part> = mutableListOf(),
  val node: Node,
  val offsetInNode: Int = -1
) : Part(
  x,
  y,
  width,
  height,
  topPadding,
  rightPadding,
  bottomPadding,
  leftPadding,
  children
) {
  override fun toString(): String {
    var additionalInfo: String = ""
    if (node is Element) {
      additionalInfo += "tag " + node.tagName()
    } else if (node is TextNode) {
      additionalInfo += "text " + U.head(node.text(), 40)
    }
    val childrenCount = children.count()
    return "InlinePart x$x y$y w$width h$height c$childrenCount $additionalInfo"
  }
}
