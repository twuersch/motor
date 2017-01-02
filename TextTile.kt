import org.jsoup.nodes.Node

/**
 * Created by timo on 01.01.17.
 */
class TextTile (
  x: Int = 0,
  y: Int = 0,
  width: Int = 0,
  height: Int = 0,
  val node: Node,
  val offsetInNode: Int = -1,
  val text: String = ""
) : Tile (
  x,
  y,
  width,
  height
) {
  override fun toString(): String {
    return "TextTile x$x y$y '$text'"
  }
}
