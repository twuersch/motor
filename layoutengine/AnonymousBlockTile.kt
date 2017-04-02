package layoutengine

/**
 * Created by timo on 01.01.17.
 */
open class AnonymousBlockTile (
  x: Int = 0,
  y: Int = 0,
  width: Int = 0,   // Note that width and height _include_ the padding.
  height: Int = 0,
  var topPadding: Int = 0,
  var rightPadding: Int = 0,
  var bottomPadding: Int = 0,
  var leftPadding: Int = 0,
  val children: MutableList<Tile> = mutableListOf(),
  val parent: AnonymousBlockTile?
) : Tile(
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
    return "layoutengine.AnonymousBlockTile x$x y$y w$width h$height $paddingInfo c$childrenCount"
  }

  fun contentHeight(): Int {
    return height - topPadding - bottomPadding
  }

  fun contentHeight(contentHeight: Int): Unit {
    height = contentHeight + topPadding + bottomPadding
  }

  fun growContentHeight(amount: Int) {
    contentHeight(contentHeight() + amount)
    parent?.growContentHeight(amount)
  }
}
