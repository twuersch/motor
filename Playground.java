import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * Created by timo on 13.11.16.
 */
public class Playground {
  public static void main(String[] args) {
    try {
      // Read the example HTML file into a string
      String html = U.readFile("test.html");

      // Clean the HTML
      Whitelist whitelist = Whitelist
        .relaxed()
        .addTags(
          "header",
          "nav",
          "section",
          "article",
          "aside",
          "footer",
          "form",
          "input",
          "fieldset",
          "title",
          "time",
          "noscript"
        )
        .removeTags(
          "col",
          "colgroup"
        );
      html = Jsoup.clean(html, whitelist);

      // Parse the HTML into a Document...
      Document document = Jsoup.parse(html);

      printDocumentStructure(document);
    } catch (Exception exception) {
      // Catch all
      throw new RuntimeException(exception);
    }
  }

  private static void printDocumentStructure(Document document) {
    assert Boolean.TRUE;
  }
}
