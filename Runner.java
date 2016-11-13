import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

public class Runner {
  public static void main (String[] args) {
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
      
      // ...and do the layouting.
      Box screen = new BlockBox(new Position(0, 0), new Size(360, 0));
      screen.padding(new Padding(0));
      Box laidOutDocument = Layouter.layout(document, screen);
      Renderer.render(laidOutDocument);
      System.out.println("done.");

    } catch (Exception exception) {
      // Catch all
      throw new RuntimeException(exception);
    }
  }
}
