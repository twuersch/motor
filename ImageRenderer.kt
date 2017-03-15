import java.awt.Color
import java.awt.Font
import java.awt.Toolkit
import java.awt.image.BufferedImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

/**
 * Created by timo on 17.02.17.
 */
object ImageRenderer {

  fun render(tile: Tile, renderText: Boolean = true, renderTextBlocks: Boolean = true) {

    // Have a look here for reference: http://www.java2s.com/Code/Java/2D-Graphics-GUI/DrawanImageandsavetopng.htm

    // List all tiles, blocks and text separately
    // TODO: depth is never used, remove
    var tilesAndDepths: MutableSet<Pair<Tile, Int>> = mutableSetOf()
    _render(tile, 0, tilesAndDepths)

    // Get dimensions
    val width = tilesAndDepths.map{ it.first.x + it.first.width }.max() ?: 0
    val height = tilesAndDepths.map{ it.first.y + it.first.height }.max() ?: 0

    // Set up image and font
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = image.createGraphics()
    val fontSize = (8 * Toolkit.getDefaultToolkit().screenResolution / 72.0).toInt()
    val font = Font("Courier New", Font.PLAIN, fontSize)
    val ascent = graphics.getFontMetrics(font).ascent
    graphics.font = font

    // Loop 1: Block tiles
    for (tileAndDepth in tilesAndDepths) {
      val tile = tileAndDepth.first
      if (tile is AnonymousBlockTile) {
        graphics.color = Color(0.0f, 0.0f, 0.0f, 0.06f)
        graphics.fillRect(tile.x, tile.y, tile.width, tile.height)
      }
    }

    // Loop 2: Text tiles
    for (tileAndDepth in tilesAndDepths) {
      val tile = tileAndDepth.first
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
    ImageIO.write(image, "PNG", File("out-" + dateString + ".png"))
    println("ImageRenderer done.")
  }

  fun _render(tile: Tile, depth: Int, tilesAndDepths: MutableSet<Pair<Tile, Int>>) {
    tilesAndDepths.add(Pair(tile, depth))
    if (tile is AnonymousBlockTile) {
      for (child in tile.children) {
        _render(child, depth + 1, tilesAndDepths)
      }
    }
  }
}
