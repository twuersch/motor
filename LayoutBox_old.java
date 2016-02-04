import java.util.List;
import java.util.Vector;
import org.jsoup.nodes.Node;

public class LayoutBox_old {

  public Node node;
  public Dimensions dimensions;
  public BoxType boxType;
  public List<LayoutBox_old> children;

  public enum BoxType {
    // TODO: Convert to class hierarchy; require node to be non-null
    Block, Inline, AnonymousBlock
  }

  public LayoutBox_old(Node node, BoxType boxType) {
    this.node = node;
    this.dimensions = new Dimensions();
    this.boxType = boxType;
    this.children = new Vector<LayoutBox_old>();
  }

  public LayoutBox_old(BoxType boxType) {
    this.node = null;
    this.dimensions = new Dimensions();
    this.boxType = boxType;
    this.children = new Vector<LayoutBox_old>();
  }

  public String toString() {
    String string = this.boxType.toString();
    if (this.node != null) {
      string += " " + U.head(this.node.toString(), 100);
    }
    return string;
  }
}
