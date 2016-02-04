public final class Coordinates {
  private final int x;
  private final int y;

  public Coordinates(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Coordinates() {
    this(0, 0);
  }


  public int x() {
    return this.x;
  }

  public int y() {
    return this.y;
  }

  public Coordinates x(int x) {
    return new Coordinates(x, this.y);
  }

  public Coordinates y(int y) {
    return new Coordinates(this.x, y);
  }
}