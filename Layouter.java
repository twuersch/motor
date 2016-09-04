import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by timo on 2016-04-17.


 Still feels far too complicated for what it does. I think I need:

 - Box with Position and Padding, in Block and Inline flavor
 (replaces all of LayoutBox, and no more separate anonymous block box type)
 - One single layout pass for the tree
 (At the moment, it's two: The first for creating the layout boxes, the second
 for calculating the layout)
 - A signature for the layouter more like: Box layout(Node node, Box parent)
 (starting with an empty parent box with the screen width and no (?) associated node)
 (or if parent == null, create screen width parent? Or another signature for that without the parent parameter?)
 - Question remains: Do boxes have nodes associated? I think yes, but if so, how do we start?

 */
public class Layouter {

  static final Set<String> blockElements = new HashSet<>(
    Arrays.asList(new String[]{
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

  static final Set<String> inlineElements = new HashSet<>(
    Arrays.asList(new String[]{
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

  public static Box layout(Node node, Box parentBox) {
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

    Box box = isInlineNode(node) ? new InlineBox(node) : new BlockBox(node);
    box.width(parentBox.width() - parentBox.leftPadding() - parentBox.rightPadding());
    box.x(parentBox.x() + parentBox.leftPadding());
    box.y(parentBox.y() + parentBox.topPadding());

    // Lay out children
    Box inlineWrapperBox = null;
    for (Node child : node.childNodes()) {
      if (isBlockNode(child)) {
        if (inlineWrapperBox != null) {
          inlineWrapperBox = null;
        }
        box.children().add(layout(child, box));
      } else {
        if (inlineWrapperBox == null) {
          inlineWrapperBox = new BlockBox();
          box.children().add(inlineWrapperBox);
        }
        inlineWrapperBox.children().add(layout(child, inlineWrapperBox));
      }
    }

    // TODO: Probably layout myself, part 2

    return box;
  }

  private static boolean isInlineNode(Node node) {
    return (node instanceof TextNode
      || node instanceof Element
      && inlineElements.contains(((Element) node).tagName()));
  }

  private static boolean isBlockNode(Node node) {
    return !isInlineNode(node);
  }
}
