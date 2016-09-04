import org.jsoup.nodes.Node;

import java.util.List;
import java.util.Vector;

/**
 * Created by timo on 2016-04-17.
 */
public abstract class Box {
  private Position position;
  private Size size;
  private Padding padding;
  private Node node;
  private List<Box> children;

  public Box() {
    this(null);
  }

  public Box(Node node) {
    this(new Position(), new Size(), new Padding(), node);
  }

  public Box(Position position, Size size) {
    this(position, size, new Padding());
  }

  public Box(Position position, Size size, Padding padding) {
    this(position, size, padding, null);
  }

  public Box(Position position, Size size, Padding padding, Node node) {
    this.position = position;
    this.size = size;
    this.padding = padding;
    this.node = node;
    this.children = new Vector<Box>();
  }

  public String toString() {
    String string = this.getClass().toString();
    if (this.node != null) {
      string += " " + U.head(this.node.toString(), 100);
    }
    return string;
  }

  public Position position() { return this.position; }

  public Size size() { return this.size; }

  public Padding padding() { return this.padding; }

  public Node node() { return this.node; }

  public List<Box> children() { return this.children; }
  
  public int width() { return this.size().width(); }
  
  public void width(int width) { this.size().width(width); }
  
  public int height() { return this.size().height(); }
  
  public void height(int height) { this.size().height(height); }
  
  public int x() { return this.position().x(); }

  public void x(int x) { this.position().x(x); }

  public int y() { return this.position().y(); }

  public void y(int y) { this.position().y(y); }
  
  public int leftPadding() { return this.padding().left(); }

  public void leftPadding(int leftPadding) { this.padding().left(leftPadding); }

  public int rightPadding() { return this.padding().right(); }

  public void rightPadding(int rightPadding) { this.padding().right(rightPadding); }

  public int topPadding() { return this.padding().top(); }

  public void topPadding(int topPadding) { this.padding().top(topPadding); }

  public int bottomPadding() { return this.padding().bottom(); }

  public void bottomPadding(int bottomPadding) { this.padding().bottom(bottomPadding); }

}
