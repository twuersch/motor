public final class Padding {
  private final int top;
  private final int right;
  private final int bottom;
  private final int left;
  public static final int DEFAULT = 5;

  public Padding(int top, int right, int bottom, int left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public Padding(int top, int leftAndRight, int bottom) {
    this(top, leftAndRight, bottom, leftAndRight);
  }

  public Padding(int topAndBottom, int leftAndRight) {
    this(topAndBottom, leftAndRight, topAndBottom, leftAndRight);
  }

  public Padding(int padding) {
    this(padding, padding, padding, padding);
  }

  public Padding() {
    this(DEFAULT, DEFAULT, DEFAULT, DEFAULT);
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

  public Padding top(int top) {
    return new Padding(top, this.right, this.bottom, this.left);
  }

  public Padding right(int right) {
    return new Padding(this.top, right, this.bottom, this.left);
  }

  public Padding bottom(int bottom) {
    return new Padding(this.top, this.right, bottom, this.left);
  }

  public Padding left(int left) {
    return new Padding(this.top, this.right, this.bottom, left);
  }

  @Override
  public String toString() {
    return "t" + this.top
      + " r" + this.right
      + " b" + this.bottom
      + " l" + this.left;
  }
}
