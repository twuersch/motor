import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

  // http://stackoverflow.com/questions/3760152/split-string-to-equal-length-substrings-in-java/3760193#3760193
  public static List<String> splitEqually(String text, int size) {
    List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

    for (int start = 0; start < text.length(); start += size) {
      ret.add(text.substring(start, Math.min(text.length(), start + size)));
    }
    return ret;
  }
}
