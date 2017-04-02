import layoutengine.AnonymousBlockTile
import layoutengine.TextTile
import layoutengine.Tile
import java.awt.Color
import java.awt.Font
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

/**
 * Renders the given root tile and its children as a PNG file.
 *
 * Java2D stuff taken mostly from
 * http://www.java2s.com/Code/Java/2D-Graphics-GUI/DrawanImageandsavetopng.htm
 *
 * @param rootTile The root tile.
 * @param renderText Display text. Default `true`.
 * @param renderTextBlocks Show text tiles as blocks. Default `true`.
 *
 * Created by timo on 17.02.17.
 */
object ImageRenderer {

  fun render(rootTile: Tile, renderText: Boolean = true, renderTextBlocks: Boolean = true) {


    var tiles: MutableSet<Tile> = mutableSetOf()
    collectAllTiles(rootTile, tiles)

    // Get dimensions
    val width = tiles.map{ it.x + it.width }.max() ?: 0
    val height = tiles.map{ it.y + it.height }.max() ?: 0

    // Set up image and font
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()
    val fontSize = (8 * Toolkit.getDefaultToolkit().screenResolution / 72.0).toInt()
    val font = Font("Courier New", Font.PLAIN, fontSize)
    val ascent = graphics.getFontMetrics(font).ascent
    graphics.font = font

    // Loop 1: Block tiles
    for (tile in tiles) {
      if (tile is AnonymousBlockTile) {
        graphics.color = Color(0.0f, 0.0f, 0.0f, 0.06f)
        graphics.fillRect(tile.x, tile.y, tile.width, tile.height)
      }
    }

    // Loop 2: Text tiles
    for (tile in tiles) {
      if (tile is TextTile) {
        if (renderTextBlocks){
          graphics.color = Color(0.0f, 0.5f, 0.0f, 0.06f)
          graphics.fillRect(tile.x, tile.y, tile.width, tile.height)
        }
        if (renderText) {
          graphics.color = Color(0.0f, 0.0f, 0.0f, 0.5f)
          graphics.drawString(tile.text, tile.x, tile.y + ascent)
        }
      }
    }

    // Write image
    val dateString = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().time)
    ImageIO.write(image, "PNG", File("out-$dateString.png"))
    println("ImageRenderer done.")
  }

  fun collectAllTiles(tile: Tile, tiles: MutableSet<Tile>) {
    tiles.add(tile)
    if (tile is AnonymousBlockTile) {
      for (child in tile.children) {
        collectAllTiles(child, tiles)
      }
    }
  }
}
