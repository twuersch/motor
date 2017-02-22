import java.awt.Font
import java.awt.image.BufferedImage

/**
 * Created by timo on 17.02.17.
 */
object ImageRenderer {

  fun render(tile: Tile) {

    // Have a look here for reference: http://www.java2s.com/Code/Java/2D-Graphics-GUI/DrawanImageandsavetopng.htm

    // List all tiles, blocks and text separately
    var blockTiles: MutableSet<Pair<AnonymousBlockTile, Int>> = mutableSetOf()
    var textTiles: MutableSet<TextTile> = mutableSetOf()
    _render(tile, 0, blockTiles, textTiles)

    // Get dimensions
    val width = Math.max(blockTiles.map{ it.first.x + it.first.width }.max() ?: 0, textTiles.map { it.x + it.width }.max() ?: 0)
    val height = Math.max(blockTiles.map{ it.first.y + it.first.height }.max() ?: 0, textTiles.map { it.y + it.height }.max() ?: 0)

    // Set up image
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()
    val font = Font("Courier New", Font.PLAIN, 8)



    println("ImageRenderer done.")
  }

  fun _render(tile: Tile, depth: Int, blockTiles: MutableSet<Pair<AnonymousBlockTile, Int>>, textTiles: MutableSet<TextTile>) {
    if (tile is AnonymousBlockTile) {
      blockTiles.add(Pair(tile, depth))
      for (child in tile.children) {
        _render(child, depth + 1, blockTiles, textTiles)
      }
    } else if (tile is TextTile) {
      textTiles.add(tile)
    }
  }
}