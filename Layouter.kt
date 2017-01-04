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

  fun layout(node: Node, parentBlock: AnonymousBlockTile) : List<Tile> {
    val tiles: MutableList<Tile> = mutableListOf()

    // What kind of node is it?
    if (hasBlockTile(node)) {
      // Lay out block node.

      // Initialize corresponding block tile
      val blockTile = BlockTile(node = node)

      // Calculate width and coordinates
      with(blockTile) {
        width = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding
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
      S.resetCursor(blockTile.width - blockTile.leftPadding - blockTile.rightPadding)

      // Lay out this block node's children
      var wrapperTile: AnonymousBlockTile? = null
      for (childNode in node.childNodes()) {
        if (hasBlockTile(childNode)) {
          // Child is a block node, clear wrapper tile
          if (wrapperTile != null) wrapperTile = null

          // Lay out this block node's block children
          blockTile.children.addAll(layout(childNode, blockTile))
        } else if (hasTextTile(childNode) || isFormatElement(childNode)) {
          // Child is an inline node, create wrapper tile if necessary
          if (wrapperTile == null) {
            wrapperTile = AnonymousBlockTile(
              width = blockTile.width - blockTile.leftPadding - blockTile.rightPadding,
              x = blockTile.x + blockTile.leftPadding,
              y = blockTile.y + blockTile.topPadding
            )
            S.resetCursor(wrapperTile.width - wrapperTile.leftPadding - wrapperTile.rightPadding)
            blockTile.children.add(wrapperTile)
          }

          // Lay out this block node's text or formatting children
          wrapperTile.children.addAll(layout(childNode, wrapperTile))
        }
      }

      // TODO: Calculate block height here.

      // Return laid out block node as a tile
      tiles.add(blockTile)
      return tiles
    } else if (node is TextNode) {
      // Lay out text node.

      // Prologue
      // TODO: There's still something wrong here. This is the wrong moment to calculate the remaining width, but when is it? Maybe set the cursor at the creation of a new block tile?
      S.remainingWidthPx = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding
      var textTiles: MutableList<TextTile> = mutableListOf()

      // Iterate word by word
      val wordsIterator = node.text().split(Regex("\\s+")).toMutableList().listIterator() // Iterator is required here to be able to modify while iterating
      while (wordsIterator.hasNext()) {
        val word = wordsIterator.next()

        // Does the current word fit onto the line?
        val wordWidthPx = stringWidthPx(word)
        if (wordWidthPx <= S.remainingWidthPx) {
          // Yes, position it
          val textTile = TextTile(
            x = S.cursorX + parentBlock.x + parentBlock.leftPadding,
            y = S.cursorY + parentBlock.y + parentBlock.topPadding,
            width = wordWidthPx,
            height = LINE_HEIGHT_PX,
            node = node,
            text = word
          )
          textTiles.add(textTile)
          S.moveCursorRightBy(wordWidthPx)
        } else {
          // No, insert a line break
          S.cursorX = 0
          S.remainingWidthPx = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding
          S.cursorY += LINE_SPACING_PX
          // Does the word fit now?
          if (wordWidthPx <= S.remainingWidthPx) {
            // Yes, position it
            val textTile = TextTile(
              x = S.cursorX + parentBlock.x + parentBlock.leftPadding,
              y = S.cursorY + parentBlock.y + parentBlock.topPadding,
              width = wordWidthPx,
              height = LINE_HEIGHT_PX,
              node = node,
              text = word
            )
            textTiles.add(textTile)
            S.moveCursorRightBy(wordWidthPx)
          } else {
            // no, break it apart and loop again
            val maxChars = Math.floor(S.remainingWidthPx.toDouble() / CHARACTER_WIDTH_PX.toDouble())
            val leftPart = word.substring(0, maxChars.toInt())
            val rightPart = word.substring(maxChars.toInt())
            wordsIterator.remove() // Remove word which is too long
            wordsIterator.add(leftPart) // insert left part into the iterator
            wordsIterator.add(rightPart) // insert right part
            wordsIterator.previous() // rewind iterator...
            wordsIterator.previous() // ...to point to left part. Iterate again.
          }
        }

        // Space between words, if possible
        if (WORD_SPACING_PX <= S.remainingWidthPx) {
          S.moveCursorRightBy(WORD_SPACING_PX)
        }
      }

      // Return laid out text node as a tile
      tiles.addAll(textTiles)
      return tiles
    } else if (isFormatElement(node)) {
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

  /**
   * Captures the layouter state across iterations
   */
  object S {
    var cursorX: Int = 0 // relative to parent block tile
    var cursorY: Int = 0 // relative to parent block tile
    var remainingWidthPx: Int = 0

    fun resetCursor(lineWidthPx: Int) {
      cursorX = 0
      remainingWidthPx = lineWidthPx
    }

    fun moveCursorRightBy(px: Int) {
      cursorX += px
      remainingWidthPx -= px
    }
  }
}
