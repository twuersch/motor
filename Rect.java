public class Rect {
  public Coordinates coordinates;
  public int width, height;
  
  public Rect() {
    this.coordinates = new Coordinates();
    this.coordinates.x = this.coordinates.y = this.width = this.height = 0;
  }
  
  public Rect(int x, int y) {
    this.coordinates = new Coordinates();
    this.coordinates.x = x;
    this.coordinates.y = y;
    this.width = this.height = 0;
  }
  
  public Rect(int x, int y, int width, int height) {
    this.coordinates.x = x;
    this.coordinates.y = y;
    this.width = width;
    this.height = height;
  }
}
