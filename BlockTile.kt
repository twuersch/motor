import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

/**
 * Created by timo on 01.01.17.
 */
class BlockTile (
  x: Int = 0,
  y: Int = 0,
  width: Int = 10,
  height: Int = 10,
  topPadding: Int = 5,
  rightPadding: Int = 5,
  bottomPadding: Int = 5,
  leftPadding: Int = 5,
  children: MutableList<Tile> = mutableListOf(),
  val node: Node
) : AnonymousBlockTile (
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
    var additionalInfo = ""
    if (node is Element) additionalInfo = node.tagName()
    val childrenCount = children.count()
    val paddingInfo = if (topPadding == rightPadding &&
      rightPadding == bottomPadding &&
      bottomPadding == leftPadding &&
      leftPadding == topPadding)
      "p$topPadding"
    else
      "p$topPadding $rightPadding $bottomPadding $leftPadding"
    return "BlockTile x$x y$y w$width h$height $paddingInfo c$childrenCount $additionalInfo"
  }
}
