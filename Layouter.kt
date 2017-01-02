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

      // Lay out this block node's children
      var wrapperTile: AnonymousBlockTile? = null
      for (childNode in node.childNodes()) {
        if (hasBlockTile(childNode)) {
          // Child is a block node, clear wrapper tile
          if (wrapperTile != null) wrapperTile = null
          blockTile.children.addAll(layout(childNode, blockTile))
        } else if (hasTextTile(childNode) || isFormatElement(childNode)) {
          // Child is an inline node, create wrapper tile if necessary
          if (wrapperTile == null) {
            wrapperTile = AnonymousBlockTile(
              width = blockTile.width - blockTile.leftPadding - blockTile.rightPadding,
              x = blockTile.x + blockTile.leftPadding,
              y = blockTile.y + blockTile.topPadding
            )
            blockTile.children.add(wrapperTile)
          }
          wrapperTile.children.addAll(layout(childNode, wrapperTile))
        }
      }

      // TODO: Calculate block height here.

      // Return laid out block node as a tile
      tiles.add(blockTile)
      return tiles
    } else if (node is TextNode) {
      // Lay out text node.

      // Initialize corresponding text tile
      val textTile = TextTile(node = node, text = node.text())

      // Return laid out text node as a tile
      tiles.add(textTile)
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
}
