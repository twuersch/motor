public class Rect {
  public int x, y, width, height;
  
  public Rect() {
    this.x = this.y = this.width = this.height = 0;
  }
  
  public Rect(int x, int y) {
    this.x = x;
    this.y = y;
    this.width = this.height = 0;
  }
  
  public Rect(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }
}
