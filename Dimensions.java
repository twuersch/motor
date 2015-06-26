/**
 * Represents some piece of content's position and the padding around it of a
 * cut-down version of a CSS box
 */
public class Dimensions {

  public Rect content;
  public Edges padding;
  
  public Dimensions() {
    this.content = new Rect();
    this.padding = new Edges();
  }
}
