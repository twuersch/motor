import java.util.List;
import java.util.Vector;

import org.jsoup.nodes.Node;

/**
 * Created by timo on 19.07.15.
 */
public abstract class LayoutBox {
  private Node node;
  private Dimensions dimensions;
  private List<LayoutBox> children;

  public LayoutBox(Node node) {
    this.node = node;
    this.dimensions = new Dimensions();
    this.children = new Vector<LayoutBox>();
  }
}
