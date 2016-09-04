public final class Position {
  private final int x;
  private final int y;

  public Position(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public Position() {
    this(0, 0);
  }


  public int x() {
    return this.x;
  }

  public int y() {
    return this.y;
  }

  public Position x(int x) {
    return new Position(x, this.y);
  }

  public Position y(int y) {
    return new Position(this.x, y);
  }
}