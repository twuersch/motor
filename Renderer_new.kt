/**
 * Created by timo on 02.01.17.
 */
object Renderer_new {
  fun render(tile: Tile, depth: Int = 0) {
    System.out.println(U.repeat("  ", depth) + tile)
    if (tile is AnonymousBlockTile) {
      for (child in tile.children) {
        render(child, depth + 1)
      }
    }
  }
}