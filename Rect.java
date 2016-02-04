public final class Rect {
  private final Coordinates coordinates;
  private final int width;
  private final int height;

  public Rect() {
    this.coordinates = new Coordinates(0, 0);
    this.width = 0;
    this.height = 0;
  }

  public Rect(int x, int y) {
    this.coordinates = new Coordinates(x, y);
    this.width = this.height = 0;
  }

  public Rect(int x, int y, int width, int height) {
    this.coordinates = new Coordinates(x, y);
    this.width = width;
    this.height = height;
  }

  public Rect(Coordinates coordinates, int width, int height) {
    this.coordinates = coordinates;
    this.width = width;
    this.height = height;
  }

  public Coordinates coordinates() {
    return this.coordinates;
  }

  public int x() {
    return this.coordinates.x();
  }

  public int y() {
    return this.coordinates.y();
  }

  public int width() {
    return this.width;
  }

  public int height() {
    return this.height;
  }

  public Rect coordinates(Coordinates coordinates) {
    return new Rect(coordinates, this.width, this.height);
  }

  public Rect x(int x) {
    return new Rect(new Coordinates(x, this.coordinates.y()), this.width, this.height);
  }

  public Rect y(int y) {
    return new Rect(new Coordinates(this.coordinates.x(), y), this.width, this.height);
  }

  public Rect width(int width) {
    return new Rect(this.coordinates, width, this.height);
  }

  public Rect height(int height) {
    return new Rect(this.coordinates, this.width, height);
  }
}
