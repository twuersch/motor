import org.jsoup.nodes.Node

/**
 * Created by timo on 31.12.16.
 */
class TextPart (
  x: Int = 0,
  y: Int = 0,
  width: Int = 0,
  height: Int = 0,
  topPadding: Int = 0,
  rightPadding: Int = 0,
  bottomPadding: Int = 0,
  leftPadding: Int = 0,
  children: MutableList<Part> = mutableListOf(),
  val node: Node,
  val offsetInNode: Int = -1,
  val text: String = ""
) : Part (
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
    return "TextPart x$x y$y text '$text'"
  }
}