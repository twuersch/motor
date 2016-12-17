import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Created by timo on 28.11.16.
 */
object LayouterNew {

  val LINE_HEIGHT_PX: Int = 20
  val CHARACTER_WIDTH_PX: Int = 20
  val WORD_SPACING_PX: Int = 5

  val BLOCK_ELEMENTS: List<String> = listOf(
    "article",
    "aside",
    "blockquote",
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

  val INLINE_ELEMENTS: List<String> = listOf(
    "a",
    "b",
    "br",
    "cite",
    "code",
    "dd",
    "dt",
    "em",
    "i",
    "img",
    "input",
    "q",
    "small",
    "span",
    "strike",
    "strong",
    "sub",
    "sup",
    "td",
    "th",
    "time",
    "u"
  )

  fun layout(node: Node, parent: Part): Part {

    /*
     * What happens here:
     *
     * The elements of the document get translated into boxes on a two-
     * dimensional space.
     * All elements get positioned in block type elements. A sequence of inline
     * elements gets wrapped in a
     * block element as well.
     *
     * Some principles:
     *
     * - A container's width is given by its parent container.
     * - A container's height is given by its contents.
     * - The space available to the contents is given by the width.
     * - It's not possible for content to make the container wider.
     * - Inline elements cannot contain block elements.
     *
     * So:
     *
     * - Put each node into either a block or inline layout box.
     * - Wrap sequences of inline elements in a (anonymous) block layout box.
     * - Calculate the width of each block layout box
     * - Lay out all children to be able to calculate their heights
     * - Calculate the height of each block layout box by calculating and
     *   summing up the heights of its child boxes
     * - If the child boxes are inline elements: Loop through all of them,
     *   measure each element's width, keep track of
     *   the remaining space on the current line, keep track of the current
     *   line's y coordinate, and insert line
     *   breaks if there is not enough remaining space.
     *
     */

    var part: Part = EmptyPart

    // Skip HTML HEAD node
    if (node is Element && node.tagName().equals("head")) {
      return part
    }

    if (isInlineNode(node)) {
      val inlinePart = InlinePart(node = node)
      inlinePart.x = parent.x
      inlinePart.y = parent.y
      part = inlinePart
    } else if (isBlockNode(node)) {
      val blockPart = BlockPart(node = node)

      // Remove padding if root document
      if (node is Document) {
        blockPart.topPadding = 0
        blockPart.rightPadding = 0
        blockPart.bottomPadding = 0
        blockPart.leftPadding = 0
      }

      blockPart.width = parent.width - parent.leftPadding - parent.rightPadding
      blockPart.x = parent.x + parent.leftPadding
      blockPart.y = parent.y + parent.topPadding

      part = blockPart
    }

    // Lay out children
    var inlineWrapperPart: Part = EmptyPart
    for (childNode in node.childNodes()) {
      if (isBlockNode(childNode)) {
        if (inlineWrapperPart !is EmptyPart) inlineWrapperPart = EmptyPart
        val childPart = layout(childNode, part)
        if (childPart != EmptyPart) part.children.add(childPart)
      } else {
        if (inlineWrapperPart is EmptyPart) {
          inlineWrapperPart = AnonymousBlockPart()
          with(inlineWrapperPart) {
            width = parent.width
            x = parent.x
            y = parent.y
          }
          part.children.add(inlineWrapperPart)
        }
        val childPart = layout(childNode, inlineWrapperPart)
        if (childPart != EmptyPart) inlineWrapperPart.children.add(childPart)
      }
    }

    /*
     * A few notes
     *
     * - Layouting text nodes: I think for each text node, I'll need a list
     *   of segments. Each segment is a line or a part of a line.
     * - For bold, italic etc. tags, these need to set a flag (or increment a counter)
     *   to be passed to the next recursion.
     */

    // TODO: Continue here

    return part
  }

  fun isInlineNode(node: Node): Boolean {
    if (node is TextNode) {
      return true
    } else if (node is Element) {
      return INLINE_ELEMENTS.contains(node.tagName())
    } else {
      return false
    }
  }

  fun isBlockNode(node: Node): Boolean {
    return !isInlineNode(node)
  }
}