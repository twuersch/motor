/**
 * Created by timo on 01.01.17.
 */
open class AnonymousBlockTile (
  x: Int = 0,
  y: Int = 0,
  width: Int = 0,
  height: Int = 0,
  var topPadding: Int = 5,
  var rightPadding: Int = 5,
  var bottomPadding: Int = 5,
  var leftPadding: Int = 5,
  val children: MutableList<Tile> = mutableListOf()
) : Tile (
  x,
  y,
  width,
  height
) {
  override fun toString(): String {
    val childrenCount = children.count()
    val paddingInfo = if (topPadding == rightPadding &&
      rightPadding == bottomPadding &&
      bottomPadding == leftPadding &&
      leftPadding == topPadding)
      "p$topPadding"
    else
      "p$topPadding $rightPadding $bottomPadding $leftPadding"
    return "AnonymousBlockTile x$x y$y w$width h$height $paddingInfo c$childrenCount"
  }
}
