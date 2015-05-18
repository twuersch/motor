import java.util.List;
import java.util.Vector;
import org.jsoup.nodes.Node;

public class LayoutBox {
  
  public Node node;
  public Dimensions dimensions;
  public BoxType boxType;
  public List<LayoutBox> children;
  
  public enum BoxType {
    Block, Inline, AnonymousBlock
  }
  
  public LayoutBox(Node node, BoxType boxType) {
    this.node = node;
    this.dimensions = new Dimensions();
    this.boxType = boxType;
    this.children = new Vector<LayoutBox>();
  }
}
