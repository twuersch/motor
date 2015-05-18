public class Edges {
  public int left, right, top, bottom;
  
  public Edges() {
    this.left = this.right = this.top = this.bottom = 0;
  }
  
  public Edges(int thickness) {
    this.left = this.right = this.top = this.bottom = thickness;
  }
  
  public Edges(int topAndBottom, int leftAndRight) {
    this.top = this.bottom = topAndBottom;
    this.left = this.right = leftAndRight;
  }
  
  public Edges(int top, int leftAndRight, int bottom) {
    this.top = top;
    this.left = this.right = leftAndRight;
    this.bottom = bottom;
  }
  
  public Edges(int top, int right, int bottom, int left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }
}
