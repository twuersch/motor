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
  val text: String = "",
  val bold: Boolean = false,
  val italic: Boolean = false
) : Tile (
  x,
  y,
  width,
  height
) {
  override fun toString(): String {
    var additionalInfo = ""
    if (bold) additionalInfo += "B "
    if (italic) additionalInfo += "I "
    return "TextTile x$x y$y w$width h$height '$text' $additionalInfo"
  }
}
