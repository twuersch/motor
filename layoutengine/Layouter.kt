package layoutengine

import layoutengine.AnonymousBlockTile
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Created by timo on 24.02.17.
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

  fun layout(node: Node, parentBlock: AnonymousBlockTile): Unit {
    if (hasBlockTile(node)) {
      layoutBlockNode(node, parentBlock)
    } else if (hasTextTile(node)) {
      layoutTextNode(node as TextNode, parentBlock)
    } else if (isFormatElement(node)) {
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
      y = parentBlock.y + parentBlock.height - parentBlock.bottomPadding,
      parent = parentBlock
    )
    parentBlock.children.add(blockTile) // Attach myself as parent's child
    parentBlock.growContentHeight(blockTile.height)

    Text.endTextBlock() // End text block

    // Lay out children nodes with this block tile as their parent
    for (childNode in node.childNodes()) {
      layout(childNode, blockTile)
    }
  }

  fun layoutTextNode(node: TextNode, parentBlock: AnonymousBlockTile) {

    // Create wrapper tile if not present
    if (!Text.inProgress()) {
      Text.beginTextBlock(parentBlock)
    }

    /*
     Iterate over words to position them.
     Note that an Iterator is required here to be able to modify the list of words
     while iterating.
     */
    val wordsIterator = node.text().split(Regex("\\s+")).toMutableList().filter { it != "" }.toMutableList().listIterator()
    while (wordsIterator.hasNext()) {
      val word = wordsIterator.next()

      // Does the current word fit onto the line?
      val wordWidthPx = stringWidthPx(word)
      if (Text.fits(wordWidthPx)) {
        // Yes, position the word
        Text.addTextTile(word, node)
        // Space between words, if possible
        if (Text.fits(WORD_SPACING_PX)) Text.moveCursorRightBy(WORD_SPACING_PX)
      } else {
        // No, insert a line break
        Text.newline()
        // Does the word fit now?
        if (Text.fits(wordWidthPx)) {
          // Yes, position the word
          Text.addTextTile(word, node)
          // Space between words, if possible
          if (Text.fits(WORD_SPACING_PX)) Text.moveCursorRightBy(WORD_SPACING_PX)
        } else {
          // no, break it apart and loop again
          val maxChars = Math.floor(Text.remainingWidthPx.toDouble() / CHARACTER_WIDTH_PX.toDouble())
          val leftPart = word.substring(0, maxChars.toInt())
          val rightPart = word.substring(maxChars.toInt())
          wordsIterator.remove() // Remove word which is too long
          wordsIterator.add(leftPart) // insert left part into the iterator
          wordsIterator.add(rightPart) // insert right part
          wordsIterator.previous() // rewind iterator...
          wordsIterator.previous() // ...to point to left part.
        }
      }
    }
  }

  fun layoutFormatElement(node: Node, parentBlock: AnonymousBlockTile) {
    val element = node as Element

    // Which format element is it?
    if (element.tagName() == "b") Text.setBold() // bold
    else if (element.tagName() == "i") Text.setItalic() // italic

    // Lay out children
    for (childNode in node.childNodes()) {
      layout(childNode, parentBlock)
    }

    // Unset formatting
    if (element.tagName() == "b") Text.unsetBold() // bold
    else if (element.tagName() == "i") Text.unsetItalic() // italic

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

  data class Interval<out T>(val min: T, val max: T)

  fun measureYExtent(tiles: Collection<Tile>): Interval<Int> {
    val minY = tiles.map(Tile::y).min() ?: 0
    val maxY = tiles.map{ tile -> tile.y + tile.height }.max() ?: 0
    return Interval(minY, maxY)
  }

  fun measureHeight(tiles: Collection<Tile>): Int {
    val yExtent = measureYExtent(tiles)
    return (yExtent.max - yExtent.min)
  }

  fun stringWidthPx(string: String): Int {
    return string.length * CHARACTER_WIDTH_PX
  }

  /**
   * Captures the state for text layout
   */
  object Text {
    var textTiles: MutableList<Tile> = mutableListOf() // This holds the text tiles we're creating in a text block
    var wrapperBlock: AnonymousBlockTile? = null
    var parentBlock: AnonymousBlockTile? = null
    var cursorX = 0 // relative to wrapper block tile
    var cursorY = 0 // relative to wrapper block tile
    var remainingWidthPx = 0
    var bold = 0 // Counter (not simple flag) to guard against bad nesting
    var italic = 0
    var lineEmpty = true

    fun inProgress(): Boolean {
      return wrapperBlock != null
    }

    /*
      (- Set wrapper padding to 0.) (TODO)
      - width = parent with - parent padding
      - height = top padding + bottom  padding, effectively 0 at the beginning
      - y = parent height - parent bottom padding + parent y
      - Attach myself as parent's child.
     */
    fun beginTextBlock(parentBlock: AnonymousBlockTile) {
      textTiles = mutableListOf()
      val wrapperBlock = AnonymousBlockTile(
        width = parentBlock.width - parentBlock.leftPadding - parentBlock.rightPadding,
        x = parentBlock.x + parentBlock.leftPadding,
        y = parentBlock.y + parentBlock.height - parentBlock.bottomPadding,
        parent = parentBlock,
        children = textTiles
      )
      Text.wrapperBlock = wrapperBlock
      parentBlock.children.add(wrapperBlock)
      Text.parentBlock = parentBlock
      cursorX = 0
      cursorY = 0
      remainingWidthPx = wrapperBlock.width - wrapperBlock.leftPadding - wrapperBlock.rightPadding
      bold = 0
      italic = 0
      lineEmpty = true
    }

    fun endTextBlock() {
      wrapperBlock = null
    }

    fun addTextTile(text: String, node: Node) {
      if (lineEmpty) wrapperBlock?.growContentHeight(LINE_SPACING_PX) // Grow the container if this is the first word on the line
      val textTile = TextTile(
        x = cursorX + (parentBlock?.x ?:0) + (parentBlock?.leftPadding ?:0),
        y = cursorY + (parentBlock?.y ?:0) + (parentBlock?.topPadding ?:0),
        width = stringWidthPx(text),
        height = LINE_HEIGHT_PX,
        node = node,
        text = text,
        bold = isBold(),
        italic = isItalic()
      )
      textTiles.add(textTile)
      lineEmpty = false
      moveCursorRightBy(stringWidthPx(text))
    }

    fun moveCursorRightBy(px: Int) {
      cursorX += px
      remainingWidthPx -= px
    }

    fun fits(widthPx: Int): Boolean = (widthPx <= remainingWidthPx)

    fun setBold() {
      bold += 1
    }

    fun unsetBold() {
      bold = Math.max(0, bold - 1)
    }

    fun isBold(): Boolean = bold > 0

    fun setItalic() {
      italic += 1
    }

    fun unsetItalic() {
      italic = Math.max(0, italic - 1)
    }

    fun isItalic(): Boolean = italic > 0

    fun newline() {
      cursorX = 0
      remainingWidthPx = (wrapperBlock?.width ?: 0)
      - (wrapperBlock?.leftPadding ?: 0)
      - (wrapperBlock?.rightPadding ?: 0)
      cursorY += LINE_SPACING_PX
      lineEmpty = true
    }
  }
}