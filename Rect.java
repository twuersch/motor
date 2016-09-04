public final class Rect {
  private final Position position;
  private final int width;
  private final int height;

  public Rect() {
    this.position = new Position(0, 0);
    this.width = 0;
    this.height = 0;
  }

  public Rect(int x, int y) {
    this.position = new Position(x, y);
    this.width = this.height = 0;
  }

  public Rect(int x, int y, int width, int height) {
    this.position = new Position(x, y);
    this.width = width;
    this.height = height;
  }

  public Rect(Position position, int width, int height) {
    this.position = position;
    this.width = width;
    this.height = height;
  }

  public Position coordinates() {
    return this.position;
  }

  public int x() {
    return this.position.x();
  }

  public int y() {
    return this.position.y();
  }

  public int width() {
    return this.width;
  }

  public int height() {
    return this.height;
  }

  public Rect coordinates(Position position) {
    return new Rect(position, this.width, this.height);
  }

  public Rect x(int x) {
    return new Rect(new Position(x, this.position.y()), this.width, this.height);
  }

  public Rect y(int y) {
    return new Rect(new Position(this.position.x(), y), this.width, this.height);
  }

  public Rect width(int width) {
    return new Rect(this.position, width, this.height);
  }

  public Rect height(int height) {
    return new Rect(this.position, this.width, height);
  }
}
