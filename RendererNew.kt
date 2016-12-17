/**
 * Created by timo on 16.12.16.
 */
object RendererNew {
  fun render(part: Part, depth: Int = 0) {
    System.out.println(U.repeat("  ", depth) + part)
    for (child in part.children) {
      render(child, depth + 1)
    }
  }
}
