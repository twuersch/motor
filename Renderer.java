import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Renderer prototype.
 *
 * Created by timo on 05/09/16.
 */
public class Renderer {

  /*
  private final static String FILENAME = "out.png";


  public static void render(Box box) {
    try {
      BufferedImage image = new BufferedImage(box.width(), box.height(), BufferedImage.TYPE_INT_RGB);
      File file = new File(FILENAME);
      ImageIO.write(image, "PNG", file);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }
   */

  public static void render(Box box) {

    String info = "";

    if (box instanceof BlockBox) {
      BlockBox blockBox = (BlockBox) box;
      info += "BlockBox ";
    } else if (box instanceof InlineBox) {
      InlineBox inlineBox = (InlineBox) box;
      info += "InlineBox ";
    }

    Node node = box.node();
    if (node != null) {
      String inline = Layouter.isInlineNode(node) ? "inline" : "block";
      if (node instanceof Element) {
        Element element = (Element) node;
        info += box.position() + " " + box.size() + " " + inline + "  " + element.tagName();
      } else if (node instanceof TextNode) {
        TextNode textNode = (TextNode) node;
        info += box.position() + " " + box.size() + " text " + U.head(((TextNode) node).text(), 40);
      }
    }

    System.out.println(info);

    for (Box child : box.children()) {
      render(child);
    }
  }
}
