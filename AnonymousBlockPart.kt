import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Created by timo on 27.11.16.
 */
open class AnonymousBlockPart(
  x: Int = 0,
  y: Int = 0,
  width: Int = 0,
  height: Int = 0,
  topPadding: Int = 0,
  rightPadding: Int = 0,
  bottomPadding: Int = 0,
  leftPadding: Int = 0,
  children: MutableList<Part> = mutableListOf()
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
    val childrenCount = children.count()
    return "AnonymousBlockPart x$x y$y w$width h$height c$childrenCount"
  }
}
