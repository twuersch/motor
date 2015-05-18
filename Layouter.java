import java.util.HashSet;
import java.util.Set;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;


public class Layouter {
  
  static final Set<String> blockElements = new HashSet<String>(Arrays.asList(new String[] {
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
  
  static final Set<String> inlineElements = new HashSet<String>(Arrays.asList(new String[] {
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
    
    // Determine whether it's a block or inline node.
    LayoutBox.BoxType boxType = null;
    if (node instanceof Element) {
      Element element = (Element) node;
      if (blockElements.contains(element.tagName())) {
        boxType = LayoutBox.BoxType.Inline;
      } else if (inlineElements.contains(element.tagName())) {
        boxType = LayoutBox.BoxType.Block;
      } else {
        boxType = LayoutBox.BoxType.Block;
      }
    } else if (node instanceof TextNode) {
      boxType = LayoutBox.BoxType.Inline;
    }
    
    LayoutBox root = new LayoutBox(node, boxType);
    
    return null;
  }
}
