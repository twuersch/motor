import org.jsoup.nodes.Node

/**
 * Created by timo on 24.02.17.
 */
object Layouter_new {

  fun layout(node: Node, parentBlock: AnonymousBlockTile): Unit {
    if (Layouter.hasBlockTile(node)) {
      layoutBlockNode(node, parentBlock)
    } else if (Layouter.hasTextTile(node)) {
      layoutTextNode(node, parentBlock)
    } else if (Layouter.isFormatElement(node)) {
      layoutFormatElement(node, parentBlock)
    }
  }

  fun layoutBlockNode(node: Node, parentBlock: AnonymousBlockTile): Unit {
    /*
    Idea: Grow from inside out: Set current block tile as parent's y,
    and after layouting, add to the parent's height.

    - Create block tile:
      (- set padding to 0 for document, html etc.)
      - width = parent with - parent padding (✓)
      - height = my top padding + my bottom padding (✓)
      - x = parent x + parent left padding (✓)
      - y = parent height - parent bottom padding + parent y (✓)  ((1) margin between blocks either here, as y += margin...)
    - Destroy text wrapper tile if present (✓)
    - Attach myself as parent's child (✓)
    - Lay out children nodes with this block tile as their parent (✓)
    - Measure content height by measuring the newly laid out children (✓)
    - Set my content height (✓)
    - Grow parent content height by my own height ((1)....or here) (✓)
     */

    val blockTile = BlockTile(
      node = node,
      width = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding,
      x = parentBlock.x + parentBlock.leftPadding,
      y = parentBlock.y + parentBlock.height - parentBlock.bottomPadding
    )

    if (TextLayoutState.wrapperBlockTile != null) {
      TextLayoutState.wrapperBlockTile = null
    }

    parentBlock.children.add(blockTile)

    for (childNode in node.childNodes()) {
      layout(childNode, blockTile)
    }

    val childrenTilesHeight = measureHeight(blockTile.children)
    blockTile.contentHeight(childrenTilesHeight)
    parentBlock.growContentHeight(blockTile.contentHeight())
  }

  fun layoutTextNode(node: Node, parentBlock: AnonymousBlockTile) {
    /*
    - Create wrapper tile if not present: (✓)
      (- Set my padding to 0.)
      - width = parent with - parent padding (✓)
      - height = top padding + bottom  padding, effectively 0 at the start (✓)
      - y = parent height - parent bottom padding + parent y (✓)
      - Attach myself as parent's child. (✓)
    - position word(s). Grow wrapper tile height and parent block height when positioning the first word of a new line
     */
  }

  fun layoutFormatElement(node: Node, parentBlock: AnonymousBlockTile) {
    /*
    - This is simple.
     */
  }

  /**
   * Captures the state for text layout
   */
  object TextLayoutState {
    var cursorX: Int = 0 // relative to parent block tile
    var cursorY: Int = 0 // relative to parent block tile
    var remainingWidthPx: Int = 0
    var bold: Int = 0 // Counter (not simple flag) to guard against bad nesting
    var italic: Int = 0
    var wrapperBlockTile: AnonymousBlockTile? = null

    fun resetCursor(lineWidthPx: Int) {
      cursorX = 0
      remainingWidthPx = lineWidthPx
    }

    fun moveCursorRightBy(px: Int) {
      cursorX += px
      remainingWidthPx -= px
    }

    fun setBold() {
      bold = bold + 1
    }

    fun unsetBold() {
      bold = Math.max(0, bold - 1)
    }

    fun isBold(): Boolean {
      return (bold > 0)
    }

    fun setItalic() {
      italic = italic + 1
    }

    fun unsetItalic() {
      italic = Math.max(0, italic - 1)
    }

    fun isItalic(): Boolean {
      return (italic > 0)
    }
  }

  data class Interval<out T>(val min: T, val max: T)

  fun measureYExtent(tiles: Collection<Tile>): Interval<Int> {
    val minY = tiles.map{ tile -> tile.y}.min() ?: 0
    val maxY = tiles.map{ tile -> tile.y + tile.height }.max() ?: 0
    return Interval(minY, maxY)
  }

  fun measureHeight(tiles: Collection<Tile>): Int {
    val yExtent = measureYExtent(tiles)
    return (yExtent.max - yExtent.min)
  }
}