import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class U {
  public static String repeat(String string, int times) {
    return new String(new char[times]).replace("\0", string);
  }
  
  public static String readFile(String filename) throws IOException {
    File file = new File(filename);
    Scanner scanner = new Scanner(file).useDelimiter("\\A");
    return scanner.hasNext() ? scanner.next() : "";
  }

  public static String head(String string, int n) {
    return string.substring(0, Math.min(string.length(), n));
  }
}
