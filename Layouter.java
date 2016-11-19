import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.*;

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

  private static int LINE_HEIGHT_PX = 20;
  private static int CHARACTER_WIDTH_PX = 8;
  private static int WORD_SPACING_PX = 5;

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

    // Skip HTML HEAD node
    if (node instanceof Element && ((Element) node).tagName().equals("head")) return null;

    // TODO: Think about this. Maybe it's too early to create an InlineBox here, depending only on the node type.
    Box box = isInlineNode(node) ? new InlineBox(node) : new BlockBox(node);
    if (node instanceof Document) {
      box.padding(new Padding(0));
    }
    box.width(parentBox.width() - parentBox.leftPadding() - parentBox.rightPadding());
    box.x(parentBox.x() + parentBox.leftPadding());
    box.y(parentBox.y() + parentBox.topPadding());

    // Lay out children
    Box inlineWrapperBox = null;
    for (Node childNode : node.childNodes()) {
      if (isBlockNode(childNode)) {
        if (inlineWrapperBox != null) {
          inlineWrapperBox = null;
        }
        Box childBox = layout(childNode, box);
        if (childBox != null) {
          box.children().add(childBox);
        }
      } else {
        if (inlineWrapperBox == null) {
          inlineWrapperBox = new BlockBox();
          inlineWrapperBox.width(parentBox.width());
          box.children().add(inlineWrapperBox);
        }
        Box childBox = layout(childNode, inlineWrapperBox);
        if (childBox != null) {
          inlineWrapperBox.children().add(childBox);
        }
      }
    }

    // TODO: Probably layout myself, part 2

    /**
     * A few notes
     *
     * - Layouting text nodes: I think for each text node, I'll need a list
     *   of segments. Each segment is a line or a part of a line.
     * - For bold, italic etc. tags, these need to set a flag (or increment a counter)
     *   to be passed to the next recursion.
     */

    if (isInlineNode(node)) {
      int x = 0, y = 0; // Coordinates are relative to parent box
      if (node instanceof TextNode) {
        TextNode textNode = (TextNode) node;
        ListIterator<String> words = new ArrayList<String>(
          Arrays.asList(textNode.text().split("\\s+"))
        ).listIterator();

        // assert Boolean.TRUE;

        // Position each word
        while (words.hasNext()) {
          String word = words.next();

          // If the word does not fit onto the current line, do a line break
          if (stringWidthPx(word) > box.width() - x) {  // Note that at the moment, we assume same-width characters
            y += LINE_HEIGHT_PX;
            box.height(box.height() + LINE_HEIGHT_PX);
            x = 0;
          }

          // If, after a line break, the word still does not fit,
          // break apart the beginning so that it fits
          if (stringWidthPx(word) > box.width() - x) {
            words.remove();
            int splitIndex = (box.width() - x) / CHARACTER_WIDTH_PX;
            words.add(word.substring(0, splitIndex));
            words.add(word.substring(splitIndex));
            words.previous();
          }

          // Position the word
          x += word.length() * CHARACTER_WIDTH_PX;
          // TODO: Add an inline box here? Actually, that's the decision to make: How is content represented?

          // Add space after word
          x += WORD_SPACING_PX;
        }
      }
    }

    return box;
  }

  public static boolean isInlineNode(Node node) {
    return (node instanceof TextNode
      || node instanceof Element
      && inlineElements.contains(((Element) node).tagName()));
  }

  private static boolean isBlockNode(Node node) {
    return !isInlineNode(node);
  }

  private static int stringWidthPx(String string) {
    return string.length() * CHARACTER_WIDTH_PX;
  }
}
