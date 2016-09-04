import org.jsoup.nodes.Node;

/**
 * Created by timo on 2016-04-17.
 */
public class BlockBox extends Box {

  public BlockBox() { super(); }

  public BlockBox(Node node) { super(node); }

  public BlockBox(Position position, Size size) { super(position, size); }

}
