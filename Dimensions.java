/**
 *
 * Content position, widht, height, plus padding.
 * Uses border-box sizing, i.e. the content width includes padding.
 *
 */
public final class Dimensions {

  private final Rect content;
  private final Edges padding;

  public Dimensions(int x, int y, int width, int height, int topPadding, int rightPadding, int bottomPadding, int leftPadding) {
    this.content = new Rect(x, y, width, height);
    this.padding = new Edges(topPadding, rightPadding, bottomPadding, leftPadding);
  }

  public Dimensions(Rect content, Edges padding) {
    this.content = content;
    this.padding = padding;
  }

  public Dimensions(Rect content) {
    this(content, new Edges());
  }

  public Dimensions(int x, int y, int width, int height, int padding) {
    this(x, y, width, height, padding, padding, padding, padding);
  }

  public Dimensions(int x, int y, int width, int height) {
    this(x, y, width, height, 0);
  }

  public Dimensions(int x, int y) {
    this(x, y, 0, 0);
  }

  public Dimensions() {
    this(0, 0, 0, 0);
  }

  public Rect content() {
    return this.content;
  }

  public Edges padding() {
    return this.padding;
  }

  public int x() {
    return this.content.x();
  }

  public int y() {
    return this.content.y();
  }

  public int width() {
    return this.content.width();
  }

  public int height() {
    return this.content.height();
  }

  public int topPadding() {
    return this.padding.top();
  }

  public int rightPadding() {
    return this.padding.right();
  }

  public int bottomPadding() {
    return this.padding.bottom();
  }

  public int leftPadding() {
    return this.padding.left();
  }

  public Dimensions content(Rect content) {
    return new Dimensions(content, this.padding);
  }

  public Dimensions padding(Edges padding) {
    return new Dimensions(this.content, padding);
  }

  public Dimensions x(int x) {
    return new Dimensions(new Rect(x, this.content.y(), this.content.width(), this.content.height()), this.padding);
  }

  public Dimensions y(int y) {
    return new Dimensions(new Rect(this.content.x(), y, this.content.width(), this.content.height()), this.padding);
  }

  public Dimensions width(int width) {
    return new Dimensions(new Rect(this.content.x(), this.content.y(), width, this.content.height()), this.padding);
  }

  public Dimensions height(int height) {
    return new Dimensions(new Rect(this.content.x(), this.content.y(), this.content.width(), height), this.padding);
  }
}
