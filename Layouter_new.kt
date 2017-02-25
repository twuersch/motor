import org.jsoup.nodes.Node

/**
 * Created by timo on 24.02.17.
 */
object Layouter_new {

  fun layout(node: Node, parentBlock: AnonymousBlockTile): Unit {
    if (Layouter.hasBlockTile(node)) {
      /*
      Idea: Grow from inside out: Set current block tile as parent's y,
      and after layouting, add to the parent's height.

      - Create block tile:
        - width = parent with - parent padding
        - height = top padding + bottom  padding
        - y = parent height - parent bottom padding ((1) margin between blocks either here...)
      - Destroy text wrapper tile if present
      - Attach myself as parent's child
      - Lay out children with this block tile as their parent
      - Set parent height to my own height plus parent padding ((1)....or here)
       */
    } else if (Layouter.hasTextTile(node)) {
      /*
      - Create wrapper tile if not present:
        - Set my padding to 0.
        - width = parent with - parent padding
        - height = top padding + bottom  padding, effectively 0 at the start
        - y = parent height - parent bottom padding
        - Attach myself as parent's child.
      - position word(s). Grow wrapper tile height and parent block height when positioning the first word of a new line
       */
    } else if (Layouter.isFormatElement(node)) {
      /*
      - This is simple.
       */
    }
  }

  object TextLayoutState {
    // Here, add a Block tile variable for the text wrapper
  }
}