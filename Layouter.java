import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;


public class Layouter {

  static final Set<String> blockElements = new HashSet<String>(Arrays.asList(new String[]{
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
  }));

  static final Set<String> inlineElements = new HashSet<String>(Arrays.asList(new String[]{
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
  }));

  public static LayoutBox layout(Node node) {

    /*
     * What happens here:
     *
     * The elements of the document get translated into boxes on a two-dimensional space.
     * All elements get positioned in block type elements. A sequence of inline elements gets wrapped in a
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
     * - Wrap sequences of inline elements in a block layout box.
     * - Calculate the width of each block layout box
     * - Lay out all children to be able to calculate their heights
     * - Calculate the height of each block layout box by calculating and summing up the heights of its child boxes
     * - If the child boxes are inline elements: Loop through all of them, measure each element's width, keep track of
     *   the remaining space on the current line, keep track of the current's line y coordinate, and insert line
     *   breaks if there is not enough remaining space.
     *
     */

    return buildLayoutTree(node);
  }

  private static LayoutBox buildLayoutTree(Node node) {
    // Determine whether this is a block or inline node.
    LayoutBox.BoxType boxType = isInlineNode(node) ? LayoutBox.BoxType.Inline : LayoutBox.BoxType.Block;

    LayoutBox root = new LayoutBox(node, boxType);
    LayoutBox anonymousBox = null;

    for (Node child : node.childNodes()) {

      if (isInlineNode(child)) {
        if (anonymousBox == null) {
          anonymousBox = new LayoutBox(LayoutBox.BoxType.AnonymousBlock);
          root.children.add(anonymousBox);
        }
        anonymousBox.children.add(buildLayoutTree(child));
      } else {
        if (anonymousBox != null) {
          anonymousBox = null;
        }
        root.children.add(buildLayoutTree(child));
      }
    }

    return root;
  }

  private static boolean isInlineNode(Node node) {
    return (node instanceof TextNode || node instanceof Element && inlineElements.contains(((Element) node).tagName()));
  }
}
