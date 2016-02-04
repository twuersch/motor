import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;


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

  public static LayoutBox_old layout(Node node, int width) {

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
     * - Wrap sequences of inline elements in a block layout box.
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

    LayoutBox_old rootLayoutBox = buildLayoutBoxes(node);
    Dimensions dimensions = new Dimensions();
    dimensions.content.width = 320;
    dimensions.content.height = 480;
    layout(rootLayoutBox, dimensions);
    return rootLayoutBox;
  }

  private static LayoutBox_old buildLayoutBoxes(Node node) {
    // Determine whether this is a block or inline node.
    LayoutBox_old.BoxType boxType = isInlineNode(node)
      ? LayoutBox_old.BoxType.Inline
      : LayoutBox_old.BoxType.Block;

    LayoutBox_old root = new LayoutBox_old(node, boxType);
    LayoutBox_old anonymousBox = null;

    for (Node child : node.childNodes()) {
      if (isInlineNode(child)) {
        if (anonymousBox == null) {
          anonymousBox = new LayoutBox_old(LayoutBox_old.BoxType.AnonymousBlock);
          root.children.add(anonymousBox);
        }
        anonymousBox.children.add(buildLayoutBoxes(child));
      } else {
        if (anonymousBox != null) {
          anonymousBox = null;
        }
        root.children.add(buildLayoutBoxes(child));
      }
    }
    return root;
  }

  private static void layout(LayoutBox_old layoutBox,
                             Dimensions containerDimensions) {
    if (layoutBox.boxType.equals(LayoutBox_old.BoxType.Block)
      || layoutBox.boxType.equals(LayoutBox_old.BoxType.AnonymousBlock)) {
      layoutBox.dimensions.content.width = calculateBlockWidth(layoutBox,
        containerDimensions);
      layoutBox.dimensions.content.coordinates
        = calculateBlockPosition(layoutBox, containerDimensions);
      for (LayoutBox_old child : layoutBox.children) {
        layout(child, layoutBox.dimensions);
        layoutBox.dimensions.content.height
          = layoutBox.dimensions.content.height
          + child.dimensions.content.height;
      }
    } else if (layoutBox.boxType.equals(LayoutBox_old.BoxType.Inline)) {
      // TODO: Verify everything up to this point, and then continue here.
      /*
       * What happens here:
       * - If we're at the start of a new line, calculate the total available
       *   space
       * - Calculate the width of each word in the inline element
       * - Keep track of the remaining space
       * - If there is no more space, create a new line by setting the y
       *   coordinate accordingly, and resetting the available width
       * - For simplifying, make each inline element a constand width and
       *   height.
       * - Think about what happens if there are block elements inside an inline
       *   element.
       *
       */
    }
  }

  private static int calculateBlockWidth(LayoutBox_old layoutBox,
                                         Dimensions containerDimensions) {
    return containerDimensions.content.width
      - containerDimensions.padding.left
      - containerDimensions.padding.right;
  }

  private static Coordinates calculateBlockPosition(
    LayoutBox_old layoutBox,
    Dimensions containerDimensions) {
    // Note that at this point, all positions are calculated in absolute values.
    int x = containerDimensions.content.coordinates.x
      + layoutBox.dimensions.padding.left;
    int y = containerDimensions.content.coordinates.y
      + containerDimensions.content.height
      + layoutBox.dimensions.padding.top;
    return new Coordinates(x, y);
  }

  private static boolean isInlineNode(Node node) {
    return (node instanceof TextNode
      || node instanceof Element
      && inlineElements.contains(((Element) node).tagName()));
  }
}
