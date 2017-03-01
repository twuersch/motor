import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Created by timo on 24.02.17.
 */
object Layouter_new {

  fun layout(node: Node, parentBlock: AnonymousBlockTile): Unit {
    if (Layouter.hasBlockTile(node)) {
      layoutBlockNode(node, parentBlock)
    } else if (Layouter.hasTextTile(node)) {
      // TODO: Explicit cast?
      layoutTextNode(node, parentBlock)
    } else if (Layouter.isFormatElement(node)) {
      layoutFormatElement(node, parentBlock)
    }
  }

  fun layoutBlockNode(node: Node, parentBlock: AnonymousBlockTile): Unit {
    /*
    Idea: Grow from inside out: Set current block tile as parent's y,
    and after layouting, add to the parent's height.
     */

    /*
    Create block tile:
    (- set padding to 0 for document, html etc.)
    - width = parent with - parent padding
    - height = my top padding + my bottom padding
    - x = parent x + parent left padding
    - y = parent height - parent bottom padding + parent y ((TODO) (1) margin between blocks either here, as y += margin...)
     */
    val blockTile = BlockTile(
      node = node,
      width = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding,
      x = parentBlock.x + parentBlock.leftPadding,
      y = parentBlock.y + parentBlock.height - parentBlock.bottomPadding
    )

    // Destroy text wrapper tile if present
    if (TextLayoutState.wrapperBlockTile != null) {
      TextLayoutState.wrapperBlockTile = null
    }

    // Attach myself as parent's child
    parentBlock.children.add(blockTile)

    // Lay out children nodes with this block tile as their parent
    for (childNode in node.childNodes()) {
      layout(childNode, blockTile)
    }

    /*
    - Measure content height by measuring the newly laid out children
    - Set my content height
    - Grow parent content height by my own height ((1)....or here)
     */
    val childrenTilesHeight = measureHeight(blockTile.children)
    blockTile.contentHeight(childrenTilesHeight)
    parentBlock.growContentHeight(blockTile.contentHeight())
  }

  fun layoutTextNode(node: TextNode, parentBlock: AnonymousBlockTile) {
    /*
    - Create wrapper tile if not present:
      (- Set my padding to 0.) (TODO)
      - width = parent with - parent padding
      - height = top padding + bottom  padding, effectively 0 at the beginning
      - y = parent height - parent bottom padding + parent y
      - Attach myself as parent's child.
     */
    if (TextLayoutState.wrapperBlockTile == null) {
      val wrapperBlockTile = AnonymousBlockTile(
        width = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding,
        y = parentBlock.y + parentBlock.height - parentBlock.bottomPadding,
      )
      parentBlock.children.add(wrapperBlockTile)
    }

    /*
     Iterate over words to position them.
     Note that an Iterator is required here to be able to modify the list of words
     while iterating.
     */
    val wordsIterator = node.text().split(Regex("\\s+")).toMutableList().listIterator()

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
    var wrapperBlockTile: AnonymousBlockTile? = null
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