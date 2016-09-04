/**
 * Created by timo on 2016-04-17.
 */
public final class Size {

  private final int width;
  private final int height;

  public Size (int width, int height) {
    this.width = width;
    this.height = height;
  }

  public Size() {
    this(0, 0);
  }

  public int width() {
    return this.width;
  }

  public int height() {
    return this.height;
  }

  public Position width(int width) {
    return new Position(width, this.height);
  }

  public Position height(int height) {
    return new Position(this.width, height);
  }
}
