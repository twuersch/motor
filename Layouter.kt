import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Created by timo on 01.01.17.
 */
object Layouter {

  val BLOCK_TILE_ELEMENTS: List<String> = listOf(
    "#root", // counts as a block element
    "article",
    "aside",
    "blockquote",
    "body", // counts as a block element
    "caption",
    "div",
    "dl",
    "fieldset",
    "footer",
    "form",
    "h1",
    "h2",
    "h3",
    "h4",
    "h5",
    "h6",
    "header",
    "html", // counts as a block element
    "li",
    "nav",
    "noscript",
    "ol",
    "p",
    "pre",
    "section",
    "table",
    "tbody",
    "tfoot",
    "thead",
    "tr",
    "ul"
  )

  val FORMATTING_ELEMENTS: List<String> = listOf(
    "a",
    "b",
    "br",
    "cite",
    "code",
    "dd",
    "dt",
    "em",
    "i",
    "q",
    "small",
    "span",
    "strike",
    "strong",
    "sub",
    "sup",
    "time",
    "u"
  )

  val CHARACTER_WIDTH_PX = 8
  val LINE_HEIGHT_PX = 16
  val LINE_SPACING_PX = 20
  val WORD_SPACING_PX = 5

  fun layout(node: Node, parentBlock: AnonymousBlockTile) : Set<Tile> {
    val tiles: MutableSet<Tile> = mutableSetOf()

    // What kind of node are we looking at?
    if (hasBlockTile(node)) {
      // Lay out block node.

      // Initialize corresponding block tile
      val blockTile = BlockTile(node = node)

      // Calculate width and coordinates
      with(blockTile) {
        width = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding
        height = blockTile.topPadding + blockTile.bottomPadding
        x = parentBlock.x + parentBlock.leftPadding
        y = parentBlock.y + parentBlock.topPadding
      }

      // Remove padding if root document or html element
      if (node is Document
        || (node is Element && node.tagName().equals("html"))) {
        with(blockTile) {
          topPadding = 0
          rightPadding = 0
          bottomPadding = 0
          leftPadding = 0
        }
      }

      // Reset the cursor, in case there will be text tiles
      TextLayoutState.resetCursor(blockTile.width - blockTile.leftPadding - blockTile.rightPadding)

      // Lay out this block node's children
      var wrapperTile: AnonymousBlockTile? = null
      for (childNode in node.childNodes()) {
        if (hasBlockTile(childNode)) {
          // Child is a block node, clear wrapper tile
          if (wrapperTile != null) {
            wrapperTile = null
          }

          // Lay out this block node's block child
          val laidOutChildrenTiles = layout(childNode, blockTile)
          blockTile.children.addAll(laidOutChildrenTiles)
          val height = measureHeight(laidOutChildrenTiles)
          blockTile.height += height
        } else if (hasTextTile(childNode) || isFormatElement(childNode)) {
          // Child is an inline node, create wrapper tile if necessary
          if (wrapperTile == null) {
            wrapperTile = AnonymousBlockTile(
              width = blockTile.width - blockTile.leftPadding - blockTile.rightPadding,
              x = blockTile.x + blockTile.leftPadding,
              y = blockTile.y + blockTile.topPadding + blockTile.height
            )
            TextLayoutState.resetCursor(wrapperTile.width - wrapperTile.leftPadding - wrapperTile.rightPadding)
            blockTile.children.add(wrapperTile)
          }

          // Lay out this block node's text or formatting children
          val laidOutChildrenTiles = layout(childNode, wrapperTile)
          wrapperTile.children.addAll(laidOutChildrenTiles)

          // Grow wrapper tile and outer block tile by the same amount if needed
          val childrenYExtent = measureYExtent(laidOutChildrenTiles)
          if (childrenYExtent.max > wrapperTile.y + wrapperTile.height) {
            wrapperTile.height += childrenYExtent.max - wrapperTile.y - wrapperTile.height
            blockTile.height +=  childrenYExtent.max - wrapperTile.y - wrapperTile.height
          }
        }
      }

      // Return laid out block node as a tile
      tiles.add(blockTile)
      return tiles
    } else if (node is TextNode) {
      // Lay out text node.

      // Prologue
      TextLayoutState.remainingWidthPx = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding
      var textTiles: MutableList<TextTile> = mutableListOf()

      // Iterate word by word
      val wordsIterator = node.text().split(Regex("\\s+")).toMutableList().listIterator() // Iterator is required here to be able to modify while iterating
      while (wordsIterator.hasNext()) {
        val word = wordsIterator.next()

        // Does the current word fit onto the line?
        val wordWidthPx = stringWidthPx(word)
        if (wordWidthPx <= TextLayoutState.remainingWidthPx) {
          // Yes, position it
          val textTile = TextTile(
            x = TextLayoutState.cursorX + parentBlock.x + parentBlock.leftPadding,
            y = TextLayoutState.cursorY + parentBlock.y + parentBlock.topPadding,
            width = wordWidthPx,
            height = LINE_HEIGHT_PX,
            node = node,
            text = word,
            bold = TextLayoutState.isBold(),
            italic = TextLayoutState.isItalic()
          )
          textTiles.add(textTile)
          TextLayoutState.moveCursorRightBy(wordWidthPx)
        } else {
          // No, insert a line break
          with (TextLayoutState) {
            cursorX = 0
            remainingWidthPx = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding
            cursorY += LINE_SPACING_PX
          }
          // Does the word fit now?
          if (wordWidthPx <= TextLayoutState.remainingWidthPx) {
            // Yes, position it
            val textTile = TextTile(
              x = TextLayoutState.cursorX + parentBlock.x + parentBlock.leftPadding,
              y = TextLayoutState.cursorY + parentBlock.y + parentBlock.topPadding,
              width = wordWidthPx,
              height = LINE_HEIGHT_PX,
              node = node,
              text = word,
              bold = TextLayoutState.isBold(),
              italic = TextLayoutState.isItalic()
            )
            textTiles.add(textTile)
            TextLayoutState.moveCursorRightBy(wordWidthPx)
          } else {
            // no, break it apart and loop again
            val maxChars = Math.floor(TextLayoutState.remainingWidthPx.toDouble() / CHARACTER_WIDTH_PX.toDouble())
            val leftPart = word.substring(0, maxChars.toInt())
            val rightPart = word.substring(maxChars.toInt())
            wordsIterator.remove() // Remove word which is too long
            wordsIterator.add(leftPart) // insert left part into the iterator
            wordsIterator.add(rightPart) // insert right part
            wordsIterator.previous() // rewind iterator...
            wordsIterator.previous() // ...to point to left part.
          }
        }

        // Space between words, if possible
        if (WORD_SPACING_PX <= TextLayoutState.remainingWidthPx) {
          TextLayoutState.moveCursorRightBy(WORD_SPACING_PX)
        }
      }

      // Return laid out text node as TextTiles
      tiles.addAll(textTiles)
      return tiles
    } else if (isFormatElement(node)) {
      val element = node as Element

      // Which format element is it?
      if (element.tagName() == "b") TextLayoutState.setBold() // bold
      else if (element.tagName() == "i") TextLayoutState.setItalic() // italic

      // Lay out children
      for (childNode in node.childNodes()) {
        tiles.addAll(layout(childNode, parentBlock))
      }

      // Unset formatting
      if (element.tagName() == "b") TextLayoutState.unsetBold() // bold
      else if (element.tagName() == "i") TextLayoutState.unsetItalic() // italic

      return tiles
    } else {
      return tiles
    }
  }

  fun hasBlockTile(node: Node): Boolean {
    return ((node is Element) && BLOCK_TILE_ELEMENTS.contains(node.tagName()))
  }

  fun hasTextTile(node: Node): Boolean {
    return (node is TextNode)
  }

  fun isFormatElement(node: Node): Boolean {
    return ((node is Element) && FORMATTING_ELEMENTS.contains(node.tagName()))
  }

  fun stringWidthPx(string: String): Int {
    return string.length * CHARACTER_WIDTH_PX
  }

  data class Interval<out T>(val min: T, val max: T)

  fun measureYExtent(tiles: Set<Tile>): Interval<Int> {
    val minY = tiles.map{ tile -> tile.y}.min() ?: 0
    val maxY = tiles.map{ tile -> tile.y + tile.height }.max() ?: 0
    return Interval(minY, maxY)
  }

  fun measureHeight(tiles: Set<Tile>): Int {
    val yExtent = measureYExtent(tiles)
    return (yExtent.max - yExtent.min)
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
}
