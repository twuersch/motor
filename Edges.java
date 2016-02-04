public final class Edges {
  private final int top;
  private final int right;
  private final int bottom;
  private final int left;

  public Edges(int top, int right, int bottom, int left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public Edges(int top, int leftAndRight, int bottom) {
    this(top, leftAndRight, bottom, leftAndRight);
  }

  public Edges(int topAndBottom, int leftAndRight) {
    this(topAndBottom, leftAndRight, topAndBottom, leftAndRight);
  }

  public Edges(int thickness) {
    this(thickness, thickness, thickness, thickness);
  }

  public Edges() {
    this(0, 0, 0, 0);
  }

  public int top() {
    return this.top;
  }

  public int right() {
    return this.right;
  }

  public int bottom() {
    return this.bottom;
  }

  public int left() {
    return this.left;
  }

  public Edges top(int top) {
    return new Edges(top, this.right, this.bottom, this.left);
  }

  public Edges right(int right) {
    return new Edges(this.top, right, this.bottom, this.left);
  }

  public Edges bottom(int bottom) {
    return new Edges(this.top, this.right, bottom, this.left);
  }

  public Edges left(int left) {
    return new Edges(this.top, this.right, this.bottom, left);
  }
}
