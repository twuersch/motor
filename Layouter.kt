import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Created by timo on 28.11.16.
 */
object Layouter {

  val LINE_HEIGHT_PX: Int = 20
  val CHARACTER_WIDTH_PX: Int = 20
  val WORD_SPACING_PX: Int = 5

  val BLOCK_ELEMENTS: List<String> = listOf(
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

    // Lay out block node
    if (isBlockNode(node)) {
      part = BlockPart(node = node)

      // Remove padding if root document
      if (node is Document) {
        with(part) {
          topPadding = 0
          rightPadding = 0
          bottomPadding = 0
          leftPadding = 0
        }
      }

      // Calculate width and coordinates
      with(part) {
        width = parent.width - parent.leftPadding - parent.rightPadding
        x = parent.x + parent.leftPadding
        y = parent.y + parent.topPadding
      }

      // Lay out this block node's children
      var inlineWrapperPart: Part = EmptyPart
      for (childNode in node.childNodes()) {

        /* The distinction between block and child nodes at this point makes
        * sure that each part contains only block parts or only inline parts
        * by wrapping inline parts with anonymous block parts where necessary */

        if (isBlockNode(childNode)) {
          // Child is a block node, clear wrapper part
          if (inlineWrapperPart !is EmptyPart) inlineWrapperPart = EmptyPart
          val childPart = layout(childNode, part)
          if (childPart != EmptyPart) part.children.add(childPart)
        } else if (isInlineNode(childNode) || childNode is TextNode) {
          // Child is an inline node, create wrapper part if necessary
          if (inlineWrapperPart is EmptyPart) {
            // Inline nodes have to be wrapped in an anonymous block part
            inlineWrapperPart = AnonymousBlockPart(width = part.width, x = part.x, y = part.y)
            part.children.add(inlineWrapperPart)
          }
          val childPart = layout(childNode, inlineWrapperPart)
          if (childPart != EmptyPart) inlineWrapperPart.children.add(childPart)
        }
      }
    } else if (isInlineNode(node)) {
      part = InlinePart(node = node)

      for (childNode in node.childNodes()) {
        val childPart = layout(childNode, part)
        if (childPart != EmptyPart) part.children.add(childPart)
      }
    } else if (node is TextNode) {
      part = TextPart(node = node, text = node.text())
      /* TODO: Think about whether this 1:1 correspondence between nodes and parts
      actually makes sense. A counterexample would be a text node which has to
      be split up into multiple text parts
      */
    }

    /*
     * A few notes
     *
     * - Layouting text nodes: I think for each text node, I'll need a list
     *   of segments. Each segment is a line or a part of a line.
     * - For bold, italic etc. tags, these need to set a flag (or increment a counter)
     *   to be passed to the next recursion.
     */

    // TODO: Continue here. Have a look at "traversing the layout tree"
    // in https://limpet.net/mbrubeck/2014/09/17/toy-layout-engine-6-block.html

    return part
  }

  fun isInlineNode(node: Node): Boolean {
    return (node is Element) && INLINE_ELEMENTS.contains(node.tagName())
  }

  fun isBlockNode(node: Node): Boolean {
    return ((node is Element) && BLOCK_ELEMENTS.contains(node.tagName()))
  }
}